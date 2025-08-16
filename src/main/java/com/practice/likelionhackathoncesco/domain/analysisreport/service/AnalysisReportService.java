package com.practice.likelionhackathoncesco.domain.analysisreport.service;

import com.amazonaws.services.s3.AmazonS3;
import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.AnalysisReportResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.AnalysisReportErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.domain.commonfile.service.FileService;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import com.practice.likelionhackathoncesco.openai.dto.request.GptAnalysisRequest;
import com.practice.likelionhackathoncesco.openai.dto.response.GptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisReportService {
  private final AmazonS3 amazonS3; // AWS SDK에서 제공하는 S3 클라이언트 객체
  private final S3Config s3Config; // 버킷 이름과 경로 등 설정 정보
  private final FileService fileService;
  private final AnalysisReportRepository analysisReportRepository;

  // 분석 리포트를 위한 등기부등본 업로드
  @Transactional
  public FileUploadResponse uploadDocuments(PathName pathName, MultipartFile file)
  {
    AnalysisReport savedReport = fileService.uploadFile(
        pathName,
        file,
        () -> AnalysisReport.builder()
            .processingStatus(ProcessingStatus.UPLOADED)
            .build(),
        analysisReportRepository,
        null);

    return FileUploadResponse.builder()
        .reportId(savedReport.getReportId())
        .fileName(savedReport.getFileName())
        .processingStatus(savedReport.getProcessingStatus()) // 엔티티 생성 시 업로드 상태로 생성
        .build();

  }

  @Transactional
  public Boolean deleteReport(Long reportId) {
    log.info("분석 리포트 삭제 요청: reportId={}", reportId);

    try {
      // FileService의 공통 삭제 메서드 활용
      Boolean result = fileService.deleteFile(reportId, analysisReportRepository);

      log.info("등기부등본 삭제 완료: reportId={}, result={}", reportId, result);
      return result;

    } catch (Exception e) {
      log.error("등기부등본 삭제 실패: reportId={}", reportId, e);
      throw new CustomException(AnalysisReportErrorCode.FILE_SERVER_ERROR);
    }
  }


  // 전월세 안전지수 로직 + 완전한 분석 리포트 반환 메소드 -> gptResponse 응답을 파싱해서 DB에 집어넣어야 함
  @Transactional
  public AnalysisReportResponse updateAnalysisReport(
      GptResponse gptResponse, GptAnalysisRequest gptAnalysisRequest, Long reportId) {

    log.info("[AnalysisReportService] 분석리포트 수정 시도 : reportId={}", reportId);
    if (gptResponse.getAddress() == null || gptResponse.getAddress()
        .isBlank()) {
      throw new CustomException(AnalysisReportErrorCode.INVALID_REPORT_ADDRESS);
    }
    if (gptResponse.getSummary() == null || gptResponse.getSummary()
        .isBlank()) {
      throw new CustomException(AnalysisReportErrorCode.INVALID_REPORT_SUMMARY);
    }
    if (gptResponse.getSafetyDescription() == null
        || gptResponse.getSafetyDescription().isBlank()) {
      throw new CustomException(AnalysisReportErrorCode.INVALID_SAFETY_DESCRIPTION);
    }
    if (gptResponse.getInsuranceDescription() == null
        || gptResponse.getInsuranceDescription().isBlank()) {
      throw new CustomException(AnalysisReportErrorCode.INVALID_INSURANCE_DESCRIPTION);
    }

    // 분석 상태 수정 -> 위험도 분석 중
    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(AnalysisReportErrorCode.REPORT_NOT_FOUND));
    analysisReport.updateProcessingStatus(ProcessingStatus.ANALYZING);

    Integer dangerNum = Integer.valueOf(gptResponse.getDangerNum()); // 위험수치의 합
    Integer dept = Integer.valueOf(gptResponse.getDept().replaceAll(",",""));
    Integer officalPrice = 0;  // 해당 매물의 공시가격 나중에 가져와서 수정해야함!!!!!!!!!!!!!!!!!!!
    Double safetyScore;

    if(dangerNum == 1){   // 안전 또는 불안 범위
      if((officalPrice-dept) >= gptAnalysisRequest.getDeposit()){   // 안전 : 7~10점
        safetyScore = 7.0 + 3 * (officalPrice-dept-gptAnalysisRequest.getDeposit())/(officalPrice-dept);
      }
      else{   // 불안 : 3~7점
        safetyScore = 3.0 + 4 * (1-(gptAnalysisRequest.getDeposit()-officalPrice-dept)/(officalPrice-dept));
      }
    }
    else{   // 위험 : 0~3점
      safetyScore = 3.0 - dangerNum;
    }

    // 분석결과로 분석리포트 수정
    analysisReport.update(gptResponse.getAddress(),
        safetyScore,
        gptResponse.getSummary(),
        gptResponse.getSafetyDescription(),
        gptResponse.getInsuranceDescription());

    // 분석 상태 수정 -> 모든 처리 완료
    analysisReport.updateProcessingStatus(ProcessingStatus.COMPLETED);

    log.info("[AnalysisReportService] 분석리포트 수정 완료 : reportId={}", reportId);

    return toAnalysisReportResponse(analysisReport);
  }

  public AnalysisReportResponse toAnalysisReportResponse(AnalysisReport analysisReport) {

    return AnalysisReportResponse.builder()
        .analysisReportUrl(amazonS3.getUrl(s3Config.getBucket(), analysisReport.getS3Key()).toString())
        .address(analysisReport.getAddress())
        .safetyScore(analysisReport.getSafetyScore())
        .summary(analysisReport.getSummary())
        .safetyDescription(analysisReport.getSafetyDescription())
        .insuranceDescription(analysisReport.getInsuranceDescription())
        .build();
  }
}
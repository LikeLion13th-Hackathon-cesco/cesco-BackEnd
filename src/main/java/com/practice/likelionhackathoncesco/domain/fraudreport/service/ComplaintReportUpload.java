package com.practice.likelionhackathoncesco.domain.fraudreport.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.AnalysisReportErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.domain.commonfile.service.FileService;
import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.ComplaintResponse;
import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.FraudRegisterResponse;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ComplaintReport;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.FraudRegisterReport;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ReportStatus;
import com.practice.likelionhackathoncesco.domain.fraudreport.repository.ComplaintReportRepository;
import com.practice.likelionhackathoncesco.domain.fraudreport.repository.FraudRegisterReportRepository;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.exception.UserErrorCode;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintReportUpload { // 고소장 업로드 서비스 로직

  private final FileService fileService;
  private final ComplaintReportRepository complaintReportRepository;

  // 분석 리포트를 위한 등기부등본 업로드
  @Transactional
  public ComplaintResponse uploadDocuments(PathName pathName, MultipartFile file)
  {
    ComplaintReport savedReport = fileService.uploadFile(
        pathName,
        file,
        () -> ComplaintReport.builder()
            .reportStatus(ReportStatus.UPLOADCOMPLETED)
            .build(),
        complaintReportRepository,
        null);

    return ComplaintResponse.builder()
        .complaintReportId(savedReport.getComplaintReportId())
        .fileName(savedReport.getFileName())
        .reportStatus(savedReport.getReportStatus()) // 엔티티 생성 시 업로드 상태로 생성
        .build();

  }

  @Transactional
  public Boolean deleteReport(Long reportId) {
    log.info("분석 리포트 삭제 요청: reportId={}", reportId);

    try {
      // FileService의 공통 삭제 메서드 활용
      Boolean result = fileService.deleteFile(reportId, complaintReportRepository);

      log.info("등기부등본 삭제 완료: reportId={}, result={}", reportId, result);
      return result;

    } catch (Exception e) {
      log.error("등기부등본 삭제 실패: reportId={}", reportId, e);
      throw new CustomException(AnalysisReportErrorCode.FILE_SERVER_ERROR);
    }
  }

}

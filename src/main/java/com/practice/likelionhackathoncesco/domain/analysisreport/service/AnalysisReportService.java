package com.practice.likelionhackathoncesco.domain.analysisreport.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.AnalysisReportErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.S3ErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.domain.commonfile.service.FileService;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.exception.UserErrorCode;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisReportService {

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
}

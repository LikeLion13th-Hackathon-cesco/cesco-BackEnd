package com.practice.likelionhackathoncesco.domain.fraudreport.service;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.commonfile.service.FileService;
import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.FraudResponse;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ComplaintReport;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.FraudRegisterReport;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ReportStatus;
import com.practice.likelionhackathoncesco.domain.fraudreport.repository.ComplaintReportRepository;
import com.practice.likelionhackathoncesco.domain.fraudreport.repository.FraudRegisterReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudReportUpload { // 사기 등기부등본 업로드 서비스 로직

  private final FileService fileService;
  private final FraudRegisterReportRepository fraudRegisterReportRepository;
  private final ComplaintReportRepository complaintReportRepository;

  // 분석 리포트를 위한 등기부등본 업로드
  @Transactional
  public FraudResponse uploadFraudDocuments(MultipartFile complaint, MultipartFile fraudReport) {

    // 고소장용 PathName
    PathName complaintPathName = PathName.COMPLAINT;

    // 등기부등본용 PathName
    PathName fraudReportPathName = PathName.FRAUDREPORT;

    // 고소장 S3업로드 후 DB 저장
    ComplaintReport savedComplaint =
        fileService.uploadFile(
            complaintPathName,
            complaint,
            () -> ComplaintReport.builder().reportStatus(ReportStatus.UPLOADCOMPLETED).build(),
            complaintReportRepository,
            null);

    // 신고 등기부등본 S3업로드 후 DB 저장
    FraudRegisterReport savedFraudReport =
        fileService.uploadFile(
            fraudReportPathName,
            fraudReport,
            () -> FraudRegisterReport.builder().reportStatus(ReportStatus.UPLOADCOMPLETED).build(),
            fraudRegisterReportRepository,
            null);

    return FraudResponse.builder()
        .complaintReportId(savedComplaint.getComplaintReportId())
        .complaintReportFileName(savedComplaint.getFileName())
        .fraudReportId(savedFraudReport.getFraudRegisterReportId())
        .fraudReportFileName(savedFraudReport.getFileName())
        .reportStatus(ReportStatus.UPLOADCOMPLETED)
        .build();
  }
}

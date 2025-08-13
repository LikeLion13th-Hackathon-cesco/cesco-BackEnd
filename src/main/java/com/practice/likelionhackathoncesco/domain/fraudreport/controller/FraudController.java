package com.practice.likelionhackathoncesco.domain.fraudreport.controller;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.ComplaintResponse;
import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.FraudRegisterResponse;
import com.practice.likelionhackathoncesco.domain.fraudreport.service.ComplaintReportUpload;
import com.practice.likelionhackathoncesco.domain.fraudreport.service.FraudRegisterReportUpload;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fraud")
@Tag(name = "Fraud Report Upload", description = "신고 관련 문서 업로드 관련 API")
public class FraudController {

  private final FraudRegisterReportUpload fraudRegisterReportUpload;
  private final ComplaintReportUpload complaintReportUpload;

  @Operation(summary = "신고 등기부등본 업로드 API", description = "신고할 등기부등본 문서를 업로드하고 문서 원본이름과 상태를 리턴하는 API")
  @PostMapping(value = "/fraud-file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<FraudRegisterResponse>> uploadFraudFile(
      @RequestParam MultipartFile file
  ) {
    FraudRegisterResponse fraudRegisterResponse = fraudRegisterReportUpload.uploadDocuments(PathName.FRAUDREPORT, file);

    return ResponseEntity.ok(BaseResponse.success("신고할 등기부등본 업로드 완료.", fraudRegisterResponse));
  }

  @Operation(summary = "신고 등기부등본 삭제 API", description = "X버튼을 눌러 업로드한 신고 등기부등본 문서를 삭제하는 API")
  @DeleteMapping(value = "/fraud-file-delete/{reportId}")
  public ResponseEntity<BaseResponse<Boolean>> deleteFraudRegisterReport(@PathVariable Long reportId) {

    log.info("파일 삭제 요청: reportId={}", reportId);

    Boolean result = fraudRegisterReportUpload.deleteReport(reportId);

    return ResponseEntity.ok(BaseResponse.success("파일이 삭제되었습니다.", result));
  }

  @Operation(summary = "고소장 업로드 API", description = "고소장 문서를 업로드하고 문서 원본이름과 상태를 리턴하는 API")
  @PostMapping(value = "/complaint-file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<ComplaintResponse>> uploadComplaintFile(
      @RequestParam MultipartFile file
  ) {
    ComplaintResponse complaintResponse = complaintReportUpload.uploadDocuments(PathName.COMPLAINT, file);

    return ResponseEntity.ok(BaseResponse.success("고소장 업로드 완료.", complaintResponse));
  }

  @Operation(summary = "고소장 삭제 API", description = "X버튼을 눌러 업로드한 고소장 문서를 삭제하는 API")
  @DeleteMapping(value = "/complaint-file-delete/{reportId}")
  public ResponseEntity<BaseResponse<Boolean>> deleteComplaintReport(@PathVariable Long reportId) {

    log.info("파일 삭제 요청: reportId={}", reportId);

    Boolean result = complaintReportUpload.deleteReport(reportId);

    return ResponseEntity.ok(BaseResponse.success("파일이 삭제되었습니다.", result));
  }

}

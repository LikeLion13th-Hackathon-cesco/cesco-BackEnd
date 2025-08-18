package com.practice.likelionhackathoncesco.domain.fraudreport.controller;

import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.FakerResponse;
import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.FraudResponse;
import com.practice.likelionhackathoncesco.domain.fraudreport.service.ComplaintReportUpload;
import com.practice.likelionhackathoncesco.domain.fraudreport.service.FakerSaveFlow;
import com.practice.likelionhackathoncesco.domain.fraudreport.service.FraudReportUpload;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fraud-reports")
@Tag(name = "Fraud Report Upload", description = "신고 관련 문서 업로드 관련 API")
public class FraudController {

  private final FraudReportUpload fraudReportUpload;
  private final ComplaintReportUpload complaintReportUpload;
  private final FakerSaveFlow fakerSaveFlow;

  @Operation(
      summary = "신고 관련 문서 모두 업로드 후 임대인 정보 저장 API",
      description = "신고 관련 문서 모두 업로드 후 제출하기 버튼을 클릭하면 업로드 상태를 리턴하는 API")
  @PostMapping(value = "/reports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<List<FakerResponse>>> saveFakerInfo(
      @Parameter(description = "고소장 업로드") @RequestParam("complaint file")
          MultipartFile complaintFile,
      @Parameter(description = "신고할 등기부등본 업로드") @RequestParam("fraud report file")
          MultipartFile fraudReportFile) {

    // s3 업로드 후 DB 저장
    FraudResponse fraudResponse =
        fraudReportUpload.uploadFraudDocuments(complaintFile, fraudReportFile);

    // 신고 등기부등본 id 조회
    Long fraudReportId = fraudResponse.getFraudReportId();

    // 사기꾼 파싱 후 faker DB에 저장
    List<FakerResponse> fakerResponseList = fakerSaveFlow.processSaveFakerInfo(fraudReportId);

    return ResponseEntity.ok(BaseResponse.success("신고당한 임대인 정보 저장 완료", fakerResponseList));
  }
}

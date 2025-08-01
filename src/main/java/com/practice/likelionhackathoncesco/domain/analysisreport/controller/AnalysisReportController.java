package com.practice.likelionhackathoncesco.domain.analysisreport.controller;

import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.service.AnalysisReportService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api")
public class AnalysisReportController {

  private final AnalysisReportService analysisReportService;

  @Operation(summary = "등기부등본 업로드 API", description = "등기부등본 문서를 업로드하고 문서 원본이름과 상태를 리턴하는 API")
  @PostMapping(value = "/file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<FileUploadResponse>> uploadImage(
      @RequestParam MultipartFile file) {

    FileUploadResponse uploadResponse = analysisReportService.uploadDocuments(PathName.PROPERTYREGISTRY, file);
    return ResponseEntity.ok(BaseResponse.success("등기부등본 업로드에 성공했습니다.", uploadResponse));
  }

  @Operation(summary = "등기부등본 파일 삭제 API", description = "X버튼을 눌러 업로드한 등기부등본 문서를 삭제")
  @DeleteMapping("/reports/{reportId}")
  public ResponseEntity<BaseResponse<Boolean>> deleteReport(@PathVariable Long reportId) {

    log.info("파일 삭제 요청: reportId={}", reportId);

    Boolean result = analysisReportService.deleteReport(reportId);

    return ResponseEntity.ok(BaseResponse.success("파일이 삭제되었습니다.", result));
  }

}

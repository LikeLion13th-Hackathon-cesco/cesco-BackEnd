package com.practice.likelionhackathoncesco.domain.analysisreport.controller;

import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.service.AnalysisReportService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api")
public class AnalysisReportController {

  private final AnalysisReportService analysisReportService;

  @Operation(summary = "문서 업로드 API", description = "문서를 업로드하고 문서 원본이름과 상태를 리턴하는 API")
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<FileUploadResponse>> uploadImage(
      @RequestParam PathName pathName, MultipartFile file) {

    FileUploadResponse uploadResponse = analysisReportService.uploadDocuments(pathName, file);
    return ResponseEntity.ok(BaseResponse.success("문서 업로드에 성공했습니다.", uploadResponse));
  }

}

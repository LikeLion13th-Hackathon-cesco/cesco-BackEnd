package com.practice.likelionhackathoncesco.domain.analysisreport.controller;

import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.AnalysisReportResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.service.AnalysisFlowService;
import com.practice.likelionhackathoncesco.domain.analysisreport.service.AnalysisReportService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import com.practice.likelionhackathoncesco.openai.dto.request.GptAnalysisRequest;
import com.practice.likelionhackathoncesco.openai.dto.response.GptResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis-reports")
@Tag(name = "Analysis Report Upload", description = "등기부등본 업로드 관련 API")
public class AnalysisReportController {

  private final AnalysisReportService analysisReportService;
  private final AnalysisFlowService analysisFlowService;

  // 안전지수, 지피티 분석 설명 반환하는 api -> 단, s3 url 가지고 파일 객체 생성해야함!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  @Operation(summary = "등기부등본 분석 결과 API", description = "분석리포트 페이지에 결과 반환")
  @PostMapping(value = "/reports/{reportId}")
  public ResponseEntity<BaseResponse<AnalysisReportResponse>> getAnalysisReport(
      @Parameter(description = "gpt-4o 분석 요청 내용") @RequestBody GptAnalysisRequest gptAnalysisRequest,
      @Parameter(description = "조회할 분석리포트 ID") @PathVariable Long reportId){
    AnalysisReportResponse analysisReportResponse = analysisFlowService.processAnalysisReport(reportId, gptAnalysisRequest);
    return ResponseEntity.ok(BaseResponse.success("분석리포트 결과 반환 완료",analysisReportResponse));
  }


  @Operation(summary = "등기부등본 업로드 API", description = "등기부등본 문서를 업로드하고 문서 원본이름과 상태를 리턴하는 API")
  @PostMapping(value = "/file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<FileUploadResponse>> uploadImage(
      @RequestParam MultipartFile file) {
    // 이 api에서는 등기부등본만 업로드 할 수 있음 (s3 PathName 고정)
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

  @Operation(summary = "업로드한 등기부등본 전체 조회 API", description = "마이페이지에서 사용자가 업로드한 등기부등본을 모두 조회하는 API")
  @GetMapping("/get-file")
  public ResponseEntity<BaseResponse<List<String>>> getAllFile() {

    log.info("S3에 업로드한 모든 등기부등본 파일 조회");

    List<String> s3files = analysisReportService.getAllS3Files(PathName.PROPERTYREGISTRY);

    return ResponseEntity.ok(BaseResponse.success("조회 성공", s3files));
  }

}

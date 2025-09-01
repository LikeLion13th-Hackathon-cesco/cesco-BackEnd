package com.practice.likelionhackathoncesco.domain.analysisreport.controller;

import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.AnalysisReportResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.service.AnalysisFlowService;
import com.practice.likelionhackathoncesco.domain.analysisreport.service.AnalysisReportService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import com.practice.likelionhackathoncesco.infra.openai.dto.request.GptAnalysisRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis-reports")
@Tag(name = "Analysis Report", description = "안전지수 분석 관련 API")
public class AnalysisReportController {

  private final AnalysisReportService analysisReportService;
  private final AnalysisFlowService analysisFlowService;

  // 안전지수, 지피티 분석 설명 반환하는 api -> 단, s3 url 가지고 파일 객체
  // 생성해야함!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  @Operation(summary = "등기부등본 분석 결과 API", description = "분석리포트 페이지에 결과 반환")
  @PostMapping(value = "/analysis-result", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public SseEmitter getAnalysisReport(
      @Parameter(description = "업로드할 파일") @RequestParam("file") MultipartFile file,
      @Parameter(description = "전월세 여부") @RequestParam("isMonthlyRent") Integer isMonthlyRent,
      @Parameter(description = "전월세 보증금") @RequestParam("deposit") Long deposit,
      @Parameter(description = "월세") @RequestParam("monthlyRent") Integer monthlyRent,
      @Parameter(description = "상세 주소") @RequestParam("detailAddress") String detailAddress,
      @Parameter(description = "예시 파일 여부") @RequestParam("isExample") Integer isExample) {

    SseEmitter emitter = new SseEmitter();

    new Thread(
            () -> {
              try {
                emitter.send(progress(10, "분석 시작", null));

                AnalysisReport savedReport =
                    analysisFlowService.uploadDocuments(
                        PathName.PROPERTYREGISTRY, file); // S3 업로드 + DB 저장(reportId 저장은 x)

                emitter.send(progress(30, "s3 업로드 완료", null));

                // ptAnalysisRequest 생성 (파일 제외하고)
                GptAnalysisRequest gptAnalysisRequest =
                    new GptAnalysisRequest(
                        null, // file은 이미 처리했으므로 null
                        isMonthlyRent,
                        deposit,
                        monthlyRent,
                        detailAddress,
                        isExample);

                AnalysisReportResponse analysisReportResponse =
                    analysisFlowService.processAnalysisReport(
                        savedReport.getReportId(), gptAnalysisRequest);

                emitter.send(progress(60, "등기부등본 분석 완료", null));

                emitter.send(progress(100, "분석리포트 결과 반환 완료", analysisReportResponse));
                emitter.complete();

              } catch (Exception e) {
                try {
                  emitter.send(progress(-1, "에러 발생: " + e.getMessage(), null));
                } catch (Exception ignore) {
                }
                emitter.completeWithError(e);
              }
            })
        .start();

    return emitter;

     AnalysisReport savedReport =
        analysisFlowService.uploadDocuments(PathName.PROPERTYREGISTRY, file); // S3 업로드 + DB 저장

    // ptAnalysisRequest 생성 (파일 제외하고)
    GptAnalysisRequest gptAnalysisRequest =
        new GptAnalysisRequest(
            null, // file은 이미 처리했으므로 null
            isMonthlyRent,
            deposit,
            monthlyRent,
            detailAddress,
            isExample);

    AnalysisReportResponse analysisReportResponse =
        analysisFlowService.processAnalysisReport(savedReport.getReportId(), gptAnalysisRequest);
    return ResponseEntity.ok(BaseResponse.success("분석리포트 결과 반환 완료", analysisReportResponse));
  }

  @Operation(summary = "등기부등본 파일 삭제 API", description = "X버튼을 눌러 업로드한 등기부등본 문서를 삭제하는 API")
  @DeleteMapping("/{reportId}")
  public ResponseEntity<BaseResponse<Boolean>> deleteReport(@PathVariable Long reportId) {

    log.info("파일 삭제 요청: reportId={}", reportId);

    Boolean result = analysisReportService.deleteReport(reportId);

    return ResponseEntity.ok(BaseResponse.success("파일이 삭제되었습니다.", result));
  }

  /*private Map<String, Object> progress(int percent, String message) {
    return Map.of("progress", percent, "message", message);
  }*/

  private Map<String, Object> progress(int percent, String message, Object data) {
    Map<String, Object> result = new HashMap<>();
    result.put("progress", percent);
    result.put("message", message);
    result.put("data", data); // null이어도 문제없음
    return result;
  }
}

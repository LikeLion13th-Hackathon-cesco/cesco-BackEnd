package com.practice.likelionhackathoncesco.naverocr.controller;

import com.practice.likelionhackathoncesco.naverocr.dto.response.OcrResponse;
import com.practice.likelionhackathoncesco.naverocr.service.NaverOcrService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
public class OcrController {

  private final NaverOcrService naverOcrService;

  // reportId를 기반으로 OCR 테스트 실행
  @Operation(summary = "등기부등본 pdf 파일 OCR로 텍스트 추출 API", description = "분석하기 버튼을 클릭하면 업로드한 파일 OCR 진행하는 API")
  @PostMapping("/{reportId}")
  public ResponseEntity<OcrResponse> testOcr(@PathVariable Long reportId) {
    OcrResponse response = naverOcrService.extractText(reportId);
    return ResponseEntity.ok(response);
  }

}

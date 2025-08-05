package com.practice.likelionhackathoncesco.domain.analysisreport.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ProcessingStatus {
  @Schema(description = "파일 업로드 완료")
  UPLOADED,

  @Schema(description = "OCR 처리 중")
  OCR_PROCESSING,

  @Schema(description = "OCR 완료")
  OCR_COMPLETED,

  @Schema(description = "위험도 분석 중")
  ANALYZING,

  @Schema(description = "GPT 설명 생성 중")
  GPT_PROCESSING,

  @Schema(description = "모든 처리 완료")
  COMPLETED,

  @Schema(description = "처리 실패")
  FAILED;
}

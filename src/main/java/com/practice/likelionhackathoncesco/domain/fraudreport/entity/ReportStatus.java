package com.practice.likelionhackathoncesco.domain.fraudreport.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ReportStatus {
  @Schema(description = "신고 관련 문서 업로드 성공")
  UPLOADCOMPLETED,

  @Schema(description = "OCR 처리 중")
  OCR_PROCESSING,

  @Schema(description = "OCR 완료")
  OCR_COMPLETED,

  @Schema(description = "신고 완료")
  REPORTCOMPLETED,

  @Schema(description = "신고 실패")
  REPORTFAILURE;
}

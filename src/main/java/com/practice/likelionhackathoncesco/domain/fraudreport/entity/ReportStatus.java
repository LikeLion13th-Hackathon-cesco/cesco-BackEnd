package com.practice.likelionhackathoncesco.domain.fraudreport.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ReportStatus {

  @Schema(description = "신고 완료")
  REPORTCOMPLETED,

  @Schema(description = "신고 실패")
  REPORTFAILURE;
}

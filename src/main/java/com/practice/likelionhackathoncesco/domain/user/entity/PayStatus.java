package com.practice.likelionhackathoncesco.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum PayStatus {
  @Schema(description = "결제 완료")
  PAID,

  @Schema(description = "결제 미완료")
  UNPAID;

}

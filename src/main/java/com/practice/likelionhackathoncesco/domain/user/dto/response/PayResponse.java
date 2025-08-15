package com.practice.likelionhackathoncesco.domain.user.dto.response;

import com.practice.likelionhackathoncesco.domain.user.entity.PayStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayResponse {

  @Schema(description = "Plus 요금제 결제 상태", example = "PAID")
  private PayStatus payStatus;

}

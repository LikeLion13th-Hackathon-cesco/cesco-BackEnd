package com.practice.likelionhackathoncesco.infra.openai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "GptDeptResponseDTO", description = "GPT 근저당 총액 응답 DTO")
public class GptDeptResponse {

  @Schema(description = "아직 말소되지 않은 근저당의 총합", example = "450000000")
  private Long dept;

  @Schema(description = "위험수치의 총합", example = "1/0/-1/-2/-3")
  private Integer dangerNum;
}

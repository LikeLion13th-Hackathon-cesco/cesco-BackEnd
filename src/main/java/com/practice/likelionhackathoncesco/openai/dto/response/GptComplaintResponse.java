package com.practice.likelionhackathoncesco.openai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "GptComplaintResponseDTO", description = "신고용 등기부등본 갑구 택스트 분석 후 응답 DTO")
public class GptComplaintResponse {

  @Schema(description = "사기 임대인 이름", example="이완용")
  private String fakerName;

  @Schema(description = "사기 임대인 주민번호 앞자리", example = "630409")
  private String residentNum;

}

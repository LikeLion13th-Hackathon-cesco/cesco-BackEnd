package com.practice.likelionhackathoncesco.openai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "GptResponseDTO", description = "분석 응답 DTO")
public class GptResponse {

  @Schema(description = "위험요소 말소 여부", example = "1")
  private Integer dangerNum;  // 가처분, 가등기, 가압류 말소 시: 1, 말소X 시: 0
  
  @Schema(description = "안전지수에 대한 요약 설명", example = "이 부동산은 거래에 주의가 필요합니다.")
  private String summary;   // 안전지수 한줄 요약

  @Schema(description = "안전지수에 대한 구체적인 설명", example = "현재 등기부등본 갑구에는 말소되지 않은 가압류 1건과 압류 1건이 존재합니다.")
  private String analysisText;   // 안전지수 구체적 설명


}

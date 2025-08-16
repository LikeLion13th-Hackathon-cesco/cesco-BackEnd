package com.practice.likelionhackathoncesco.openai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "GptResponseDTO", description = "분석 응답 DTO")
public class GptResponse {

  @Schema(description = "위험요소 말소 여부", example = "1")
  private String dangerNum; // 가처분, 가등기, 가압류 말소 시: 1, 말소X 시: 0

  @Schema(description = "해당 등기부등본 부동산 주소", example = "서울시 송파대로 48길 29")
  private String address;

  @Schema(description = "안전지수에 대한 요약 설명", example = "이 부동산은 거래에 주의가 필요합니다.")
  private String summary; // 안전지수 한줄 요약

  @Schema(description = "안전지수에 대한 구체적인 설명", example = "현재 등기부등본 갑구에는 말소되지 않은 가압류 1건과 압류 1건이 존재합니다.")
  private String safetyDescription; // 안전지수 구체적 설명

  @Schema(description = "아직 말소되지 않은 근저당의 총합", example = "450000000")
  private String dept;

  @Schema(description = "보증보험 가입 가능 여부 분석 설명", example = "보증보험 심사에서 부정적으로 평가될 수 있어요.")
  private String insuranceDescription; // 보증보험 가입 가능 여부 설명
}

package com.practice.likelionhackathoncesco.infra.openai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(title = "GptSecRequestDTO", description = "GPT 두번째 요청 DTO")
public class GptSecRequest {

  // private Integer safetyData; // 안전지수를 위한 중간 데이터

  private Integer insuranceData; // 보험을 위한 중간 데이터

  private Double safetyScore; // summary와 description을 응답으로 받기 위한 요청

  private String safetyScoreStatus; // 불안/안전/위험 상태
}

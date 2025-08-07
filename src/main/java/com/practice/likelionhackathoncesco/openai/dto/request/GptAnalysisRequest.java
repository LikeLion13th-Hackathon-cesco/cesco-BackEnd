package com.practice.likelionhackathoncesco.openai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "GptAnalysisRequestDTO", description = "텍스트 분석 요청 DTO")
public class GptAnalysisRequest {

  @Schema(description = "분석할 텍스트", example = "...")
  private String text;   // ocr 분석 후 추출된 텍스트


}

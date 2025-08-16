package com.practice.likelionhackathoncesco.openai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "GptAnalysisRequestDTO", description = "텍스트 분석 요청 DTO")
public class GptAnalysisRequest {

  //@Schema(description = "분석할 텍스트", example = "...")
  //private String text;   // ocr 분석 후 추출된 텍스트

  @Schema(description = "전월세 여부", example = "0")
  private Integer isMonthlyRent;  // 월세일 경우 : 1 , 전세일 경우 : 0

  @Schema(description = "전월세 보증금", example = "100000000")   //1억
  private Integer deposit;  // 전월세 보증금

  @Schema(description = "월세", example = "3000000")
  private Integer monthlyRent;  // 전세인 경우 0으로 설정 필요^^^^^^^^^^

  //@Schema(description = "공시가격", example = "450000000")    //4.5억
  //private Long officialPrice;   // 공시가격

}

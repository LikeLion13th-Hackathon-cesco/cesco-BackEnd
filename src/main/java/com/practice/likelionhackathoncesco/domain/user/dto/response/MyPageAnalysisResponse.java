package com.practice.likelionhackathoncesco.domain.user.dto.response;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageAnalysisResponse { // 나중에 AnalysisResponse로 묶어도 될듯!!!!

  @Schema(description = "분석레포트 id", example = "1")
  private Long reportId;

  @Schema(description = "해당 부동산의 주소", example = "서울시 송파구 송파대로 48길 29")
  private String address;

  @Schema(description = "전월세 안전지수", example = "7.5")
  private Double safetyScore;

  @Schema(description = "분석 한줄 설명", example = "이 부동산은 거래에 주의가 필요합니다.")
  private String summary;

  @Schema(description = "안전 점수에 따른 한 줄 코멘트", example = "해당 부동산은 거래 시 위험 부담이 있습니다")
  private Comment comment;
}

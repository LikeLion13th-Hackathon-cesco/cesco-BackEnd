package com.practice.likelionhackathoncesco.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponse {

  @Schema(description = "사용자 보유 크레딧", example = "1000")
  private Integer credit;

  @Schema(description = "게시글 작성 개수", example = "3")
  private Integer postCount;

  @Schema(description = "사용자 분석 리포트 리스트", example = ".")
  private List<MyPageAnalysisResponse> reports;
}

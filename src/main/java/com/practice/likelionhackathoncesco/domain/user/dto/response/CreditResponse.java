package com.practice.likelionhackathoncesco.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "CreditResponse DTO", description = "사용자별 크레딧 지급 기준 충족 여부 응답")
public class CreditResponse {

  @Schema(description = "게시글 1개 작성", example = "true")
  private Boolean postCountOne;

  @Schema(description = "게시글 5개 작성", example = "false")
  private Boolean postCountFive;

  @Schema(description = "게시글 15개 작성", example = "false")
  private Boolean postCountFifteen;
}

package com.practice.likelionhackathoncesco.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "LikeResponse DTO", description = "좋아요 응답")
public class LikeResponse {

  @Schema(description = "사용자 고유 번호", example = "1")
  private Long userId;

  @Schema(description = "게시글 고유 번호", example = "1")
  private Long postId;
}

package com.practice.likelionhackathoncesco.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "LikeResponse DTO", description = "좋아요 응답")
public class LikeResponse {

  // 사용자 ID 추가 예정?
  
  @Schema(description = "게시글 고유 id", example = "1")
  private Long postId;

  @Schema(description = "커뮤니티 고유 id", example = "1")
  private Long communityId;
}

package com.practice.likelionhackathoncesco.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(title="CreatePostRequest DTO", description="게시글 생성 요청")
public class CreatePostRequest {

  @Schema(description = "게시글 대상 커뮤니티 ID", example = "1")
  private Long communityId;

  @Schema(description = "게시글 내용", example = "보안이 별로 안좋은듯")
  private String content;

}

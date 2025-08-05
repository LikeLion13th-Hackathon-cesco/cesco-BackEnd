package com.practice.likelionhackathoncesco.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(title="CreateCommentRequest DTO", description="댓글 생성 요청")
public class CreateCommentRequest {

  @Schema(description = "댓글 작성 사용자 ID", example = "1")
  private Long userId;

  @Schema(description = "댓글이 달린 게시글 ID", example = "1")
  private Long postId;
  
  @Schema(description = "댓글 내용", example = "근처 병원은 주로 어디로 가세요?")
  private String content;

}

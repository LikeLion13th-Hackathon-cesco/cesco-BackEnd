package com.practice.likelionhackathoncesco.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(title = "UpdateCommentRequest DTO", description = "댓글 수정 요청")
public class UpdateCommentRequest {

  @Schema(description = "수정할 댓글 내용", example = "PC방 어디로 가시나요?")
  private String content;
}

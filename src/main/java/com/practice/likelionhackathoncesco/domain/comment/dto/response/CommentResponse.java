package com.practice.likelionhackathoncesco.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "CommentResponse DTO", description = "댓글 응답")
public class CommentResponse {

  @Schema(description = "댓글 고유번호", example = "1")
  private Long commentId;

  @Schema(description = "댓글 내용", example = "202호 매물 보고 있었는데 절대 가지 말아야겠네요.")
  private String content;

  @Schema(description = "댓글 작성자 고유 번호")
  private Long userId;

  @Schema(description = "댓글이 달린 게시글 고유 번호")
  private Long postId;

  @Schema(description = "생성일시", example = "2025-11-29T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "수정일시", example = "2025-11-29T01:00:00")
  private LocalDateTime updatedAt;

}

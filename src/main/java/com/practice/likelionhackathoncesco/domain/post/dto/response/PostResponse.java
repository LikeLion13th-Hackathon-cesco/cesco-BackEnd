package com.practice.likelionhackathoncesco.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PostResponse DTO", description = "게시글 응답")
public class PostResponse {

  @Schema(description = "게시글 고유번호", example = "1")
  private Long postId;

  @Schema(description = "게시글 내용", example = "302호 층간소음이 너무 심해서 계약일까지 존버합니다..")
  private String content;

  @Schema(description = "게시글에 대한 좋아요 수", example = "1")
  private Long likeCount;

  @Schema(description = "게시글에 대한 댓글 수", example = "1")
  private Long commentCount;

  @Schema(description = "게시글 작성자 고유 번호")
  private Long userId; // 익명이어도 글쓴이만 글쓴이(게시글과 댓글에)라고 뜨게 할 예정

  @Schema(description = "생성일시", example = "2025-11-29T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "수정일시", example = "2025-11-29T01:00:00")
  private LocalDateTime modifiedAt;
}

package com.practice.likelionhackathoncesco.domain.post.dto.response;

import com.practice.likelionhackathoncesco.domain.post.entity.Post;
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
  
  @Schema(description = "게시글에 대한 좋아요 수")
  private Long likeCount;

  @Schema(description = "생성일시", example = "2025-11-29T00:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "수정일시", example = "2025-11-29T01:00:00")
  private LocalDateTime modifiedAt;
}

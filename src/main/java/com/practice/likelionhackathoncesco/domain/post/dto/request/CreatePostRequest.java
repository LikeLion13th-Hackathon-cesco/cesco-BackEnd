package com.practice.likelionhackathoncesco.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(title = "CreatePostRequest DTO", description = "게시글 생성 요청")
public class CreatePostRequest {

  @Schema(description = "게시글 내용", example = "보안이 별로 안좋은듯")
  private String content;

  @Schema(description = "해당 게시글 속한 커뮤니티의 도로명코드", example = "117104169350")
  private String roadCode;

  @Schema(description = "해당 게시글이 속한 커뮤니티의 건물본번", example = "29")
  private String buildingNumber;

  @Schema(description = "게시글 작성 사용자 ID", example = "1")
  private Long userId;
}

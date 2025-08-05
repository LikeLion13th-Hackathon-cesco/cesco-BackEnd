package com.practice.likelionhackathoncesco.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@Schema(title = "UpdatePostRequest DTO", description = "게시글 수정 요청")
public class UpdatePostRequest {

  @Schema(description = "수정할 게시글 내용", example = "이 집은 아닙니다..")
  private String content;

}

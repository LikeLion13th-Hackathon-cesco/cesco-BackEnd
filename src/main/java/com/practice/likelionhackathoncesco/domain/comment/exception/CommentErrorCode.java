package com.practice.likelionhackathoncesco.domain.comment.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {
  COMMENT_NOT_FOUND("COMMENT_4001", "해당 댓글을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  INVALID_COMMENT_CONTENT("COMMENT_4002", "댓글 내용은 필수입니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

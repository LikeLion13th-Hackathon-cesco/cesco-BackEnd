package com.practice.likelionhackathoncesco.domain.like.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LikeErrorCode implements BaseErrorCode {
  LIKE_NOT_FOUND("LIKE_4002", "해당 좋아요를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  LIKE_IS_EXIST("LIKE_4003", "이미 존재하는 좋아요 입니다", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

package com.practice.likelionhackathoncesco.global.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {
  INVALID_INPUT_VALUE("G001", "유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST),
  RESOURCE_NOT_FOUND("G002", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INTERNAL_SERVER_ERROR("G003", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

package com.practice.likelionhackathoncesco.domain.user.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  USER_ALREADY_PAID("USER_ALREADY_PAID", "이미 결제 완료된 사용자 입니다.", HttpStatus.CONFLICT),
  UNPAID_USER_REPORT_LIMIT_EXCEEDED(
      "UNPAID_USER_REPORT_LIMIT_EXCEEDED",
      "무료 사용자는 최대 3개의 분석리포트만 이용할 수 있습니다.",
      HttpStatus.FORBIDDEN);
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}

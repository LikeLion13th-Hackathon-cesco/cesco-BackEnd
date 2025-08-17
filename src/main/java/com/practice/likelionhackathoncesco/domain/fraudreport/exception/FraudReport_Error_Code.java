package com.practice.likelionhackathoncesco.domain.fraudreport.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FraudReport_Error_Code implements BaseErrorCode {
  FRAUD_REPORT_NOT_FOUND("FRAUD_REPORT_4001", "존재하지 않는 신고용 등기부등본 입니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

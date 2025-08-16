package com.practice.likelionhackathoncesco.naverocr.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OcrErrorCode implements BaseErrorCode {

  // OCR API 관련 에러
  OCR_API_CALL_FAILED("OCR5001", "OCR API 호출에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  OCR_API_TIMEOUT("OCR4001", "OCR API 호출이 시간 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT),
  OCR_API_RATE_LIMIT_EXCEEDED("OCR4002", "OCR API 호출 한도를 초과했습니다.", HttpStatus.TOO_MANY_REQUESTS),
  OCR_API_UNAUTHORIZED("OCR4003", "OCR API 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
  OCR_API_BAD_REQUEST("OCR4004", "OCR API 요청 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

  // OCR 처리 관련 에러
  OCR_PROCESSING_FAILED("OCR5003", "OCR 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  OCR_NO_TEXT_DETECTED("OCR4201", "텍스트를 감지할 수 없습니다.", HttpStatus.BAD_REQUEST),
  OCR_CONFIDENCE_TOO_LOW("OCR4202", "OCR 인식 정확도가 너무 낮습니다.", HttpStatus.BAD_REQUEST),
  OCR_RESPONSE_PARSING_FAILED("OCR5004", "OCR 응답 파싱에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // 설정 관련 에러
  OCR_CONFIG_MISSING("OCR5301", "OCR API 설정 정보가 누락되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  OCR_SECRET_KEY_INVALID("OCR5302", "OCR API 비밀키가 유효하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  OCR_INVOKE_URL_INVALID("OCR5303", "OCR API URL이 유효하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // 일반 에러
  OCR_INTERNAL_ERROR("OCR5500", "OCR 서비스 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  OCR_SERVICE_UNAVAILABLE("OCR5503", "OCR 서비스를 사용할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

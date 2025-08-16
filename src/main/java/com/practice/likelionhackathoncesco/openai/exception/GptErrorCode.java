package com.practice.likelionhackathoncesco.openai.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GptErrorCode implements BaseErrorCode {

  GPT_API_CALL_FAILED("GPT_5001", "GPT API 호출에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  GPT_RESPONSE_PARSING_FAILED("GPT_5002", "GPT 응답 파싱에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  GPT_TIMEOUT("GPT_5003", "GPT 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT),
  GPT_INVALID_PROMPT("GPT_4001", "GPT 요청에 포함된 프롬프트가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  GPT_EMPTY_RESPONSE("GPT_5004", "GPT로부터 받은 응답이 비어 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;

}

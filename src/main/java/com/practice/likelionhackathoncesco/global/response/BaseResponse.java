package com.practice.likelionhackathoncesco.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "BaseResponse DTO", description = "공통 API 응답 형식")
public class BaseResponse<T> { // 응답 클래스

  @Schema(description = "요청 성공 여부", example = "true")
  private boolean success;

  @Schema(description = "HTTP 상태 코드", example = "200")
  private int code;

  @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
  private String message;

  @Schema(description = "응답 데이터")
  private T data;

  public static <T> BaseResponse<T> success(T data) {
    return new BaseResponse<>(true, 200, "요청이 성공적으로 처리되었습니다.", data);
  }

  public static <T> BaseResponse<T> success(String message, T data) {
    return new BaseResponse<>(true, 200, message, data);
  }

  public static <T> BaseResponse<T> error(int code, String message) {
    return new BaseResponse<>(false, code, message, null);
  }
}

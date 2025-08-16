package com.practice.likelionhackathoncesco.domain.analysisreport.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {
  FILE_NOT_FOUND("PDF4001", "존재하지 않는 PDF 파일입니다.", HttpStatus.NOT_FOUND),
  FILE_NAME_NOT_INVAILD("PDF4002", "유효하지 않은 파일 이름입니다.", HttpStatus.BAD_REQUEST),
  FILE_SIZE_INVALID("PDF4002", "파일 크기는 50MB를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
  FILE_TYPE_INVALID("PDF4003", "PDF 파일만 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
  FILE_SERVER_ERROR(
      "PDF5001", "PDF파일 처리 중 서버 에러, 관리자에게 문의 바랍니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_BASE64("PDF4005", "잘못된 Base64값 입니다.", HttpStatus.BAD_REQUEST),
  FILE_DOWNLOAD_FAIL("PDF4006", "S3에서 다운로드를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}

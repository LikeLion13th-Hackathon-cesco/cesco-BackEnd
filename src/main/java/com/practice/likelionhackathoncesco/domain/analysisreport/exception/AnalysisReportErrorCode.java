package com.practice.likelionhackathoncesco.domain.analysisreport.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnalysisReportErrorCode implements BaseErrorCode {

  FILE_NOT_FOUND("IMG4001", "존재하지 않는 파일입니다.", HttpStatus.NOT_FOUND),
  FILE_SIZE_INVALID("IMG4002", "파일 크기는 5MB를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
  FILE_TYPE_INVALID("IMG4003", "PDF 파일만 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
  FILE_SERVER_ERROR("IMG5001", "PDF 처리 중 서버 에러, 관리자에게 문의 바랍니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_BASE64("IMG4005", "잘못된 Base64값 입니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;


}

package com.practice.likelionhackathoncesco.domain.quiz.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum QuizErrorCode implements BaseErrorCode {

  QUIZ_NOT_FOUND("QUIZ_4001", "해당 퀴즈를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);


  private final String code;
  private final String message;
  private final HttpStatus status;

}

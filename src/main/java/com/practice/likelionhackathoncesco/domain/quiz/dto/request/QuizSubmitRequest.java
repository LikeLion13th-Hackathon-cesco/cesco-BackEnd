package com.practice.likelionhackathoncesco.domain.quiz.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(title = "QuizSubmitRequest DTO", description = "퀴즈 풀이 응답 요청")
public class QuizSubmitRequest {

  @Schema(description = "사용자가 고른 선택지", example = "3")
  private Integer selectedOption;

  @Schema(description = "퀴즈 고유번호", example = "1")
  private Long quizId;

  @Schema(description = "고정 사용자 고유번호", example = "1")
  private Long userId;
}

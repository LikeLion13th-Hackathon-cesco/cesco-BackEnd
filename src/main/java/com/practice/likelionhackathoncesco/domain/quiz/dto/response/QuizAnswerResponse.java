package com.practice.likelionhackathoncesco.domain.quiz.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "QuizAnswerResponse DTO", description = "퀴즈 풀이 후 응답")
public class QuizAnswerResponse {

  @Schema(description = "퀴즈 고유 번호", example = "1")
  private Long quizId;

  @Schema(description = "퀴즈 문제", example = "다음중 전입신고에 대한 설명으로 옳지 않은 것은?")
  private String title;

  @Schema(description = "선택지 1", example = "보증금 보호를 위해 전입신고는 필수다.")
  private String optionOne;

  @Schema(description = "선택지 2", example = "전입신고는 동사무소나 온라인에서 할 수 있다.")
  private String optionTwo;

  @Schema(description = "선택지 3", example = "전입신고를 하면 확정일자가 자동으로 부여된다.")
  private String optionThree;

  @Schema(description = "선택지 4", example = "전입신고를 하면 주민등록 주소가 해당 주소로 변경된다.")
  private String optionFour;

  @Schema(description = "정답", example = "3")
  private Integer correctAnswer;

  @Schema(description = "사용자가 고른 선택지의 정답 여부", example = "1")
  private Boolean isCorrect;

  @Schema(description = "정답에 대한 해설", example = "전입신고가 되었더라도 확정일자는 별도로 받아야 합니다.")
  private String explanation;
}

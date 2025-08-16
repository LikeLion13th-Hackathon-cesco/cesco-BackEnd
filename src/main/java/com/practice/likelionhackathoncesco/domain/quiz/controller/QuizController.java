package com.practice.likelionhackathoncesco.domain.quiz.controller;

import com.practice.likelionhackathoncesco.domain.quiz.dto.request.QuizSubmitRequest;
import com.practice.likelionhackathoncesco.domain.quiz.dto.response.QuizAnswerResponse;
import com.practice.likelionhackathoncesco.domain.quiz.dto.response.QuizResponse;
import com.practice.likelionhackathoncesco.domain.quiz.service.QuizService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "quiz", description = "퀴즈 관련 API")
public class QuizController {

  private final QuizService quizService;

  // 고정 사용자가 아직 풀지 않은 퀴즈 중 랜덤으로 단일 조회 API
  @Operation(summary = "퀴즈 단일 조회", description = "체크리스트 페이지에서 퀴즈풀기 버튼 눌렀을때 호출되는 API")
  @GetMapping("/quiz")
  public ResponseEntity<BaseResponse<QuizResponse>> getQuizByIsSolvedIsZero() {
    QuizResponse quizResponse = quizService.getRandomQuiz();
    return ResponseEntity.ok(BaseResponse.success("퀴즈 랜덤 단일 조회 성공", quizResponse));
  }

  // 고정 사용자가 퀴즈를 푼 후, 결과 응답 API
  @Operation(summary = "퀴즈 풀이 후 결과 응답", description = "퀴즈 풀이 후 확인하기 버튼 누르면 호출되는 API")
  @PutMapping("/quiz/result")
  public ResponseEntity<BaseResponse<QuizAnswerResponse>> getResultBySummitAnswer(
      @Parameter(description = "퀴즈 풀이 제출 내용") @RequestBody QuizSubmitRequest quizSubmitRequest) {
    QuizAnswerResponse quizAnswerResponse = quizService.submitAnswer(quizSubmitRequest);
    return ResponseEntity.ok(BaseResponse.success("퀴즈 풀이 후 결과 응답 성공", quizAnswerResponse));
  }
}

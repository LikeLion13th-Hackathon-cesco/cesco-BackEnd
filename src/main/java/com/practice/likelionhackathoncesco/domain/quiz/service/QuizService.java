package com.practice.likelionhackathoncesco.domain.quiz.service;

import com.practice.likelionhackathoncesco.domain.quiz.dto.request.QuizSubmitRequest;
import com.practice.likelionhackathoncesco.domain.quiz.dto.response.QuizResponse;
import com.practice.likelionhackathoncesco.domain.quiz.entity.Quiz;
import com.practice.likelionhackathoncesco.domain.quiz.entity.QuizAnswer;
import com.practice.likelionhackathoncesco.domain.quiz.exception.QuizErrorCode;
import com.practice.likelionhackathoncesco.domain.quiz.repository.QuizAnswerRepository;
import com.practice.likelionhackathoncesco.domain.quiz.repository.QuizRepository;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.exception.UserErrorCode;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

  private final QuizRepository quizRepository;
  private final UserRepository userRepository;
  private final QuizAnswerRepository quizAnswerRepository;

  // 고정 사용자가 퀴즈를 푼 후 결과 응답 및 크레딧 추가하는 메소드
  @Transactional
  public QuizResponse submitAnswer(QuizSubmitRequest quizSubmitRequest) {
    User user = userRepository.findById(quizSubmitRequest.getUserId()).orElseThrow(()->new CustomException(UserErrorCode.USER_NOT_FOUND));
    Quiz quiz = quizRepository.findById(quizSubmitRequest.getQuizId()).orElseThrow(()->new CustomException(QuizErrorCode.QUIZ_NOT_FOUND));

    boolean isCorrect = quiz.getCorrectAnswer().equals(quizSubmitRequest.getSelectedOption());

    QuizAnswer answer = QuizAnswer.builder()
        .selectedOption(quizSubmitRequest.getSelectedOption())
        .isCorrect(isCorrect)
        .quiz(quiz)
        .user(user)
        .build();

    quizAnswerRepository.save(answer);

    if(isCorrect){
      user.addCredits(500);
    }
    userRepository.save(user);

    return toQuizResponse(quiz);
  }



  // 고정 사용자가 풀었는지 여부 변수값이 0 or 1인지에 따라서 퀴즈 전체 조회 메소드
  @Transactional
  public List<Quiz> getAllQuizIsSolvedIsZero() {
    return quizRepository.findAllByIsSolved(0);
  }
  
  // 고정 사용자가 아직 풀지 않은 퀴즈에 대해 랜덤으로 퀴즈 단일 조회
  @Transactional
  public QuizResponse getRandomQuiz() {
    List<Quiz>quizList = getAllQuizIsSolvedIsZero();

    if(quizList == null || quizList.isEmpty()) {
      throw new CustomException(QuizErrorCode.QUIZ_NOT_FOUND);
    }

    Random random = new Random();
    Quiz quiz = quizList.get(random.nextInt(quizList.size()));  // 퀴즈 하나 랜덤으로 조회

    quiz.setIsSolved(1);  // 조회하고 나면 isSolved 변수값을 1로 변경
    quizRepository.save(quiz);  // 변경한 값대로 다시 저장

    return toQuizResponse(quiz);
  }

  public QuizResponse toQuizResponse(Quiz quiz) {
    return QuizResponse.builder()
        .title(quiz.getTitle())
        .optionOne(quiz.getOptionOne())
        .optionTwo(quiz.getOptionTwo())
        .optionThree(quiz.getOptionThree())
        .optionFour(quiz.getOptionFour())
        .correctAnswer(quiz.getCorrectAnswer())
        .explanation(quiz.getExplanation())
        .build();
  }

}

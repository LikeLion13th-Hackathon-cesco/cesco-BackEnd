package com.practice.likelionhackathoncesco.domain.quiz;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class QuizDataInitializer {

  @Autowired private JdbcTemplate jdbcTemplate;

  @PostConstruct
  public void initializeQuizData() {
    System.out.println("Quiz 더미 데이터 초기화를 시작합니다...");

    try {
      // Quiz 1 업데이트 (전입신고 문제로 덮어쓰기)
      String quiz1Update =
          """
                INSERT INTO quiz (
                    quiz_id,
                    title,
                    option_one,
                    option_two,
                    option_three,
                    option_four,
                    correct_answer,
                    explanation,
                    is_solved
                ) VALUES (
                    1,
                    '다음 중 전입신고에 대한 설명으로 옳지 않은 것은?',
                    '보증금 보호를 위해 전입신고는 필수다',
                    '전입신고는 동사무소나 온라인에서 할 수 있다',
                    '전입신고만 하면 확정일자가 자동으로 부여된다',
                    '전입신고를 하면 주민등록 주소가 해당 집으로 변경된다',
                    3,
                    '확정일자는 전입신고와 별도로 주민센터에서 신청해야 한다.',
                    0
                )
                ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                option_one = VALUES(option_one),
                option_two = VALUES(option_two),
                option_three = VALUES(option_three),
                option_four = VALUES(option_four),
                correct_answer = VALUES(correct_answer),
                explanation = VALUES(explanation),
                is_solved = VALUES(is_solved)
                """;
      jdbcTemplate.execute(quiz1Update);

      // Quiz 2 - 임대인 문제
      String quiz2 =
          """
                INSERT INTO quiz (
                    quiz_id,
                    title,
                    option_one,
                    option_two,
                    option_three,
                    option_four,
                    correct_answer,
                    explanation,
                    is_solved
                ) VALUES (
                    2,
                    '다음 중 임대인에 대한 설명으로 옳지 않은 것은?',
                    '집을 빌려주는 사람을 임대인이라고 한다',
                    '전세 계약을 체결할 때 임대인의 동의는 필요하다',
                    '임차인은 임대인에게 월세를 지급해야 한다',
                    '임대인은 항상 등기부등본상 소유자이다',
                    4,
                    '반드시 등기부등본 상의 소유자가 임대인과 일치하는 것은 아니다.',
                    0
                )
                ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                option_one = VALUES(option_one),
                option_two = VALUES(option_two),
                option_three = VALUES(option_three),
                option_four = VALUES(option_four),
                correct_answer = VALUES(correct_answer),
                explanation = VALUES(explanation),
                is_solved = VALUES(is_solved)
                """;
      jdbcTemplate.execute(quiz2);

      // Quiz 3 - 임차인 문제
      String quiz3 =
          """
                INSERT INTO quiz (
                    quiz_id,
                    title,
                    option_one,
                    option_two,
                    option_three,
                    option_four,
                    correct_answer,
                    explanation,
                    is_solved
                ) VALUES (
                    3,
                    '다음 중 임차인에 대한 설명으로 옳은 것은?',
                    '임차인은 보증금을 집주인에게 맡긴다',
                    '임차인은 등기부등본에 자동으로 등기가 올라간다',
                    '임차인은 계약 갱신이 가능하다',
                    '임차인은 집을 빌려 쓰는 사람이다',
                    2,
                    '임차인의 이름은 등기부등본에 자동으로 등재되지 않는다.',
                    0
                )
                ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                option_one = VALUES(option_one),
                option_two = VALUES(option_two),
                option_three = VALUES(option_three),
                option_four = VALUES(option_four),
                correct_answer = VALUES(correct_answer),
                explanation = VALUES(explanation),
                is_solved = VALUES(is_solved)
                """;
      jdbcTemplate.execute(quiz3);

      // Quiz 4 - 등기부등본 문제
      String quiz4 =
          """
                INSERT INTO quiz (
                    quiz_id,
                    title,
                    option_one,
                    option_two,
                    option_three,
                    option_four,
                    correct_answer,
                    explanation,
                    is_solved
                ) VALUES (
                    4,
                    '등기부등본에 대한 설명으로 옳지 않은 것은?',
                    '집의 소유자 정보를 확인할 수 있다',
                    '담보대출(근저당) 내역을 알 수 있다',
                    '등기부등본은 무조건 한장이다',
                    '전세 계약 전 반드시 확인해야 할 서류이다',
                    3,
                    '등기부등본은 여러 장일 수 있다.',
                    0
                )
                ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                option_one = VALUES(option_one),
                option_two = VALUES(option_two),
                option_three = VALUES(option_three),
                option_four = VALUES(option_four),
                correct_answer = VALUES(correct_answer),
                explanation = VALUES(explanation),
                is_solved = VALUES(is_solved)
                """;
      jdbcTemplate.execute(quiz4);

      System.out.println("Quiz 더미 데이터 초기화가 완료되었습니다!");

    } catch (Exception e) {
      System.err.println("Quiz 더미 데이터 초기화 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

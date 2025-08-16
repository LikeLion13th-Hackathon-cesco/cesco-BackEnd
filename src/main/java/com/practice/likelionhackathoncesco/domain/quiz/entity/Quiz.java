package com.practice.likelionhackathoncesco.domain.quiz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz")
public class Quiz {

  @Id
  @Column(name = "quiz_id", nullable = false)
  private Long quizId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String optionOne;

  @Column(nullable = false)
  private String optionTwo;

  @Column(nullable = false)
  private String optionThree;

  @Column(nullable = false)
  private String optionFour;

  @Column(nullable = false)
  private Integer correctAnswer;

  @Column(nullable = false)
  private String explanation; // 정답해설(옳지 않은 선택지에 한해서만)

  @Column(nullable = false)
  private Integer isSolved; // 고정 사용자가 이미 풀었는지 아닌지 여부
}

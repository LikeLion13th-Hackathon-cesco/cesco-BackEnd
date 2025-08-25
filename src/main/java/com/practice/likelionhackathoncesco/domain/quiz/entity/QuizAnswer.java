package com.practice.likelionhackathoncesco.domain.quiz.entity;

import com.practice.likelionhackathoncesco.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "quiz_answer")
public class QuizAnswer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long quizAnswerId;

  // 선택지
  @Column(nullable = false)
  private Integer selectedOption;

  // 정답 여부
  @Column(nullable = false)
  private Boolean isCorrect;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_id", nullable = false)
  private Quiz quiz;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // 사용자 ID(FK)
}

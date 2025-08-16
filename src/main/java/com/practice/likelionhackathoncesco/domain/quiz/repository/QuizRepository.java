package com.practice.likelionhackathoncesco.domain.quiz.repository;

import com.practice.likelionhackathoncesco.domain.quiz.entity.Quiz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

  List<Quiz> findAllByIsSolved(Integer isSolved);
}

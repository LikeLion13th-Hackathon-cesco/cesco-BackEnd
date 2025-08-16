package com.practice.likelionhackathoncesco.domain.quiz.repository;

import com.practice.likelionhackathoncesco.domain.quiz.entity.Quiz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

  List<Quiz> findAllByIsSolved(Integer isSolved);
}

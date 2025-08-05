package com.practice.likelionhackathoncesco.domain.comment.repository;

import com.practice.likelionhackathoncesco.domain.comment.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 게시글 별 댓글 전체 조회
  List<Comment> findAllByPostPostId(Long postId);
}

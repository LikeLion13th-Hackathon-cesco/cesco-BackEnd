package com.practice.likelionhackathoncesco.domain.like.repository;

import com.practice.likelionhackathoncesco.domain.like.entity.Like;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

  // 이미 존재하는 좋아요인지 확인하기 위해 -> DB 차원에서 해결함 for 동시성 고려
  // boolean existsByUserAndPost(User user, Post post);

  // (사용자 + 게시글) 별 좋아요 조회
  Optional<Like> findByUserAndPost(User user, Post post);

  // 사용자, 게시글 별 좋아요 존재 여부 확인 메소드
  Boolean existsByUserUserIdAndPost(Long userId, Post post);

  // 좋아요 개수 조회를 위해
  long countByPost(Post post);
}

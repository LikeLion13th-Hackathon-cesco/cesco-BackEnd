package com.practice.likelionhackathoncesco.domain.like.repository;

import com.practice.likelionhackathoncesco.domain.community.entity.Community;
import com.practice.likelionhackathoncesco.domain.like.entity.Like;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

  // 게시글과 커뮤니티를 이용해서 유일한 좋아욜
  boolean existsByPostAndCommunity(Post post, Community community);
  
  // 좋아요 개수 조회를 위해
  long countByPost(Post post);
}

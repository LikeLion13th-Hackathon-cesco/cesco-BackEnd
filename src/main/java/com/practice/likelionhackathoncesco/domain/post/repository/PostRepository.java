package com.practice.likelionhackathoncesco.domain.post.repository;

import com.practice.likelionhackathoncesco.domain.post.dto.response.PostResponse;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostRepository extends JpaRepository<Post, Long> {

  // 커뮤니티 별 게시글 전체 조회
  List<Post> findAllByCommunityId(Long communityId);
  
  // 커뮤니티 별 게시글 최신순으로 조회
  List<Post> findAllByCommunityIdOrderByCreatedAtDesc(Long communityId);

  // 커뮤니티 별 게시글 인기순(좋아요 순)으로 조회
  List<Post> findAllByCommunityIdOrderByLikeCountDesc(Long communityId);


}

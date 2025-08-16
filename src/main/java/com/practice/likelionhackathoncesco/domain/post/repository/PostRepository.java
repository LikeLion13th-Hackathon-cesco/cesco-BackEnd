package com.practice.likelionhackathoncesco.domain.post.repository;

import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

  // 도로명코드+건물본번 별 게시글 전체 조회
  List<Post> findAllByRoadCodeAndBuildingNumber(String roadCode, String buildingNumber);

  // 도로명코드+건물본번 별 게시글 최신순으로 조회
  List<Post> findAllByRoadCodeAndBuildingNumberOrderByCreatedAtDesc(
      String roadCode, String buildingNumber);

  // 도로명코드+건물본번 별 게시글 인기순(좋아요 순)으로 조회
  List<Post> findAllByRoadCodeAndBuildingNumberOrderByLikeCountDesc(
      String roadCode, String buildingNumber);
}

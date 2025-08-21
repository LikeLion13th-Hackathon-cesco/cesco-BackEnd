package com.practice.likelionhackathoncesco.domain.post.mapper;

import com.practice.likelionhackathoncesco.domain.post.dto.response.PostResponse;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

  public PostResponse toPostResponse(
      Post post, long likeCount, long commentCount) { // 좋아요 개수는 service 단에서 계산해서 넣을 예정
    return PostResponse.builder()
        .postId(post.getPostId())
        .content(post.getContent())
        .likeCount(likeCount) // 서비스 단에서 계산
        .commentCount(commentCount) // 서비스 단에서 계산
        .userId(post.getUser().getUserId()) // 익명이어도 댓글 영역에 게시글 작성자 표시를 위해
        .createdAt(post.getCreatedAt())
        .modifiedAt(post.getModifiedAt())
        .build();
  }
}

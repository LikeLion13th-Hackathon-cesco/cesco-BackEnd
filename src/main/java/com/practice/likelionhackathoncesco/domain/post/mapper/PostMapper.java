package com.practice.likelionhackathoncesco.domain.post.mapper;

import com.practice.likelionhackathoncesco.domain.post.dto.response.PostResponse;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

  public PostResponse toPostResponse(Post post, long likeCount) {
    return PostResponse.builder()
        .postId(post.getPostId())
        .content(post.getContent())
        .likeCount(likeCount)
        .createdAt(post.getCreatedAt())
        .modifiedAt(post.getModifiedAt())
        .build();
  }
}

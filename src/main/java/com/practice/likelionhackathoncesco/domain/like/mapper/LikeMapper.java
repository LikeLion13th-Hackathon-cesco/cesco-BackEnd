package com.practice.likelionhackathoncesco.domain.like.mapper;

import com.practice.likelionhackathoncesco.domain.like.dto.response.LikeResponse;
import com.practice.likelionhackathoncesco.domain.like.entity.Like;
import org.springframework.stereotype.Component;

@Component
public class LikeMapper {

  public LikeResponse toLikeResponse(Like like) {

    return LikeResponse.builder()
        .userId(like.getUser().getUserId())
        .postId(like.getPost().getPostId())
        .build();
  }
}

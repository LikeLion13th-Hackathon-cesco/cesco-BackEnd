package com.practice.likelionhackathoncesco.domain.comment.mapper;

import com.practice.likelionhackathoncesco.domain.comment.dto.response.CommentResponse;
import com.practice.likelionhackathoncesco.domain.comment.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

  public CommentResponse toCommentResponse(Comment comment) {
    return CommentResponse.builder()
        .commentId(comment.getCommentId())
        .content(comment.getContent())
        .userId(comment.getUser().getUserId())
        .postId(comment.getPost().getPostId())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getModifiedAt())
        .build();
  }
}

package com.practice.likelionhackathoncesco.domain.comment.service;

import com.practice.likelionhackathoncesco.domain.comment.dto.request.CreateCommentRequest;
import com.practice.likelionhackathoncesco.domain.comment.dto.request.UpdateCommentRequest;
import com.practice.likelionhackathoncesco.domain.comment.dto.response.CommentResponse;
import com.practice.likelionhackathoncesco.domain.comment.entity.Comment;
import com.practice.likelionhackathoncesco.domain.comment.exception.CommentErrorCode;
import com.practice.likelionhackathoncesco.domain.comment.mapper.CommentMapper;
import com.practice.likelionhackathoncesco.domain.comment.repository.CommentRepository;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import com.practice.likelionhackathoncesco.domain.post.repository.PostRepository;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.exception.UserErrorCode;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final CommentMapper commentMapper;

  // 댓글 생성
  @Transactional
  public CommentResponse createComment(CreateCommentRequest createCommentRequest) {
    log.info("[CommentService] 댓글 생성 시도 : userId={}, postId={}, content={}", createCommentRequest.getUserId(), createCommentRequest.getPostId(), createCommentRequest.getContent());
    if(createCommentRequest.getContent() == null || createCommentRequest.getContent().isBlank()){
      throw new CustomException(CommentErrorCode.INVALID_COMMENT_CONTENT);
    }
    User user = userRepository.findById(createCommentRequest.getUserId()).orElseThrow(()-> new CustomException(
        UserErrorCode.USER_NOT_FOUND));
    Post post = postRepository.findById(createCommentRequest.getPostId()).orElseThrow(()-> new CustomException(
        CommentErrorCode.COMMENT_NOT_FOUND));

    Comment comment = Comment.builder()
        .user(user)
        .post(post)
        .content(createCommentRequest.getContent())
        .build();

    commentRepository.save(comment);
    
    log.info("[CommentService] 댓글 생성 왼료 : userId={}, postId={}, content={}", comment.getUser().getUserId(), comment.getPost().getPostId(), comment.getContent());
    return commentMapper.toCommentResponse(comment);
  }

  // 댓글 삭제
  @Transactional
  public Boolean deleteComment(Long commentId) {
    log.info("[CommentService] 댓글 삭제 시도 : commentId={}", commentId);

    Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));

    log.info("[CommentService] 댓글 삭제 완료 : commentId={}", commentId);
    commentRepository.delete(comment);
    return true;
  }

  // 댓글 수정
  @Transactional
  public CommentResponse updateComment(UpdateCommentRequest updateCommentRequest, Long commentId) {
    log.info("[CommentService] 댓글 수정 시도 : commentId={}, newContent={}", commentId, updateCommentRequest.getContent());
    if(updateCommentRequest.getContent() == null || updateCommentRequest.getContent().isBlank()){
      throw new CustomException(CommentErrorCode.INVALID_COMMENT_CONTENT);
    }

    Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));
    comment.update(updateCommentRequest.getContent());

    log.info("[CommentService] 댓글 수정 완료 : commentId={}, newContent={}", comment.getCommentId(), comment.getContent());

    return commentMapper.toCommentResponse(comment);
  }


  // 게시글 별 댓글 전체 조회
  @Transactional
  public List<CommentResponse> getAllCommentsByPostId(Long postId) {
    log.info("[CommentService] 게시글 별 댓글 전체 조회");
    List<Comment> commentList = commentRepository.findAllByPostPostId(postId);

    return commentList.stream().map(comment -> commentMapper.toCommentResponse(comment)).toList();
  }

}

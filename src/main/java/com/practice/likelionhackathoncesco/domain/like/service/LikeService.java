package com.practice.likelionhackathoncesco.domain.like.service;

import com.practice.likelionhackathoncesco.domain.like.dto.response.LikeResponse;
import com.practice.likelionhackathoncesco.domain.like.entity.Like;
import com.practice.likelionhackathoncesco.domain.like.exception.LikeErrorCode;
import com.practice.likelionhackathoncesco.domain.like.mapper.LikeMapper;
import com.practice.likelionhackathoncesco.domain.like.repository.LikeRepository;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import com.practice.likelionhackathoncesco.domain.post.exception.PostErrorCode;
import com.practice.likelionhackathoncesco.domain.post.repository.PostRepository;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.exception.UserErrorCode;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

  private final PostRepository postRepository;
  private final LikeRepository likeRepository;
  private final UserRepository userRepository;
  private final LikeMapper likeMapper;

  // 좋아요 생성
  @Transactional
  public LikeResponse createLike(Long userId, Long postId) {
    log.info("[LikeService] 좋아요 생성 시도: userId={}, postId={}", userId, postId);

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(
                () -> {
                  log.warn("[LikeService] 해당 게시글이 존재하지 않음 - 좋아요 생성 실패");
                  return new CustomException(PostErrorCode.POST_NOT_FOUND);
                });

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.warn("[LikeService] 해당 사용자가 존재하지 않음 - 좋아요 생성 실패");
                  return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });

    Like like = Like.builder().user(user).post(post).build();

    try {
      likeRepository.save(like);
    } catch (DataIntegrityViolationException e) {
      log.warn("[LikeService] 이미 좋아요 누른 상태 - 생성 실패");
      throw new CustomException(LikeErrorCode.LIKE_IS_EXIST);
    }

    log.info(
        "[LikeService] 좋아요 생성 완료: userId={}, postId={}",
        like.getUser().getUserId(),
        like.getPost().getPostId());
    return likeMapper.toLikeResponse(like);
  }

  // 좋아요 삭제
  @Transactional
  public Boolean deleteLike(Long userId, Long postId) {
    log.info("[LikeService] 좋아요 삭제 시도: userId={}, postId={}", userId, postId);

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(
                () -> {
                  log.warn("[LikeService] 해당 게시글이 존재하지 않음 - 좋아요 삭제 실패");
                  return new CustomException(PostErrorCode.POST_NOT_FOUND);
                });

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.warn("[LikeService] 해당 사용자가 존재하지 않음 - 좋아요 삭제 실패");
                  return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });

    Like like =
        likeRepository
            .findByUserAndPost(user, post)
            .orElseThrow(
                () -> {
                  log.warn("[LikeService] 좋아요가 존재하지 않음 - 삭제 실패");
                  return new CustomException(LikeErrorCode.LIKE_NOT_FOUND);
                });

    likeRepository.delete(like);
    log.info(
        "[LikeService] 좋아요 삭제 완료: userId={}, postId={}",
        like.getUser().getUserId(),
        like.getPost().getPostId());

    return true;
  }
}

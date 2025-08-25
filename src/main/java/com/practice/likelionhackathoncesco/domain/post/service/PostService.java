package com.practice.likelionhackathoncesco.domain.post.service;

import com.practice.likelionhackathoncesco.domain.comment.repository.CommentRepository;
import com.practice.likelionhackathoncesco.domain.like.repository.LikeRepository;
import com.practice.likelionhackathoncesco.domain.post.dto.request.CreatePostRequest;
import com.practice.likelionhackathoncesco.domain.post.dto.request.UpdatePostRequest;
import com.practice.likelionhackathoncesco.domain.post.dto.response.PostResponse;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import com.practice.likelionhackathoncesco.domain.post.exception.PostErrorCode;
import com.practice.likelionhackathoncesco.domain.post.mapper.PostMapper;
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
public class PostService {

  private final PostRepository postRepository;
  private final PostMapper postMapper;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final LikeRepository likeRepository;

  // 게시글 생성
  @Transactional
  public PostResponse createPost(
      CreatePostRequest createPostRequest) { // user는 인증객체 X , 그냥 고정된 더미데이터 쓸거라서 파라미터에 user 추가 X

    log.info(
        "[PostService] 게시글 생성 시도 : userId={}, content={}, roadCode={}, buildingNumber={}",
        createPostRequest.getUserId(),
        createPostRequest.getContent(),
        createPostRequest.getRoadCode(),
        createPostRequest.getBuildingNumber());

    if (createPostRequest.getContent() == null || createPostRequest.getContent().isBlank()) {
      throw new CustomException(PostErrorCode.INVALID_POST_CONTENT);
    }

    User user =
        userRepository
            .findById(createPostRequest.getUserId())
            .orElseThrow(
                () ->
                    new CustomException(
                        UserErrorCode
                            .USER_NOT_FOUND)); // createPostRequest의 userId로 user 찾아서 builder로 post
    // 객체 만들때 씀

    user.addPostCount(); // 게시글 생성 시 필드 값 하나 추가

    // 게시글 개수 별 크레딧 차등 지급
    long postCount = user.getPostCount();
    if (postCount == 1) {
      user.addCredits(500);
    } else if (postCount == 5) {
      user.addCredits(700);
    } else if (postCount == 15) {
      user.addCredits(1000);
    }

    Post post =
        Post.builder()
            .content(createPostRequest.getContent())
            .user(user)
            .roadCode(createPostRequest.getRoadCode())
            .buildingNumber(createPostRequest.getBuildingNumber())
            .build();

    postRepository.save(post);

    log.info(
        "[PostService] 게시글 생성 완료 : postId={}, userId={}, content={}, roadCode={}, buildingNumber={}",
        post.getPostId(),
        post.getUser().getUserId(),
        post.getContent(),
        post.getRoadCode(),
        post.getBuildingNumber());

    return postMapper.toPostResponse(post, post.getLikeCount(), 0L);
  }

  // 게시글 삭제 - 댓글 좋아요 같이 삭제 되야함 ^^^^^^^^^^^^^^^^^
  @Transactional
  public boolean deletePost(Long postId) {
    log.info("[PostService] 게시글 삭제 시도 : postId = {}", postId);

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

    postRepository.delete(post);
    log.info("[PostService] 게시글 삭제 완료 : postId = {}", postId);

    return true;
  }

  // 게시글 수정
  @Transactional
  public PostResponse updatePost(UpdatePostRequest updatePostRequest, Long postId) {
    log.info(
        "[PostService] 게시글 수정 시도 : postId={}, newContent={}",
        postId,
        updatePostRequest.getContent());

    if (updatePostRequest.getContent() == null || updatePostRequest.getContent().isBlank()) {
      throw new CustomException(PostErrorCode.INVALID_POST_CONTENT);
    }

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

    post.update(updatePostRequest.getContent());

    long commentCount = commentRepository.countByPostPostId(postId);

    log.info("[PostService] 게시글 수정 완료 : postId={}, content = {}", postId, post.getContent());

    return postMapper.toPostResponse(post, post.getLikeCount(), commentCount);
  }

  // 게시글 단일 조회 기능
  @Transactional
  public PostResponse getPostById(Long postId) {
    log.info("[PostService] 게시글 단일 조회 시도 : postId = {}", postId);
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

    long commentCount = commentRepository.countByPostPostId(postId);

    log.info("[PostService] 게시글 단일 조회 완료 : postId = {}", postId);
    Long userId = 1L;

    boolean likedByUser = likeRepository.existsByUserUserIdAndPost(userId, post);

    return postMapper.toGetPostResponse(post, post.getLikeCount(), commentCount, likedByUser);
  }

  // (도로명코드+건물본번) 별 게시글 전체 조회 기능
  @Transactional
  public List<PostResponse> getAllPostsByRoadCodeAndBuildingNumber(
      String roadCode, String buildingNumber) {
    log.info("[PostService] (도로명코드+건물본번) 별 게시글 전체 조회 시도");
    List<Post> postList =
        postRepository.findAllByRoadCodeAndBuildingNumber(roadCode, buildingNumber);

    if (postList == null || postList.isEmpty()) {
      throw new CustomException(PostErrorCode.POST_NOT_FOUND);
    }
    Long userId = 1L;
    return postList.stream()
        .map(
            post -> {
              long commentCount = commentRepository.countByPostPostId(post.getPostId());
              boolean likedByUser = likeRepository.existsByUserUserIdAndPost(userId, post);
              return postMapper.toGetPostResponse(
                  post, post.getLikeCount(), commentCount, likedByUser);
            })
        .toList();
  }

  // (도로명코드+건물본번) 별 게시글 최신순 조회 기능
  @Transactional
  public List<PostResponse> getAllPostsByRoadCodeAndBuildingNumberAndCreatedAtDesc(
      String roadCode, String buildingNumber) {
    log.info("[PostService] (도로명코드+건물본번) 별 게시글 최신순 조회 시도");
    List<Post> postListCreatedAtDesc =
        postRepository.findAllByRoadCodeAndBuildingNumberOrderByCreatedAtDesc(
            roadCode, buildingNumber);
    Long userId = 1L;
    return postListCreatedAtDesc.stream()
        .map(
            post -> {
              long commentCount = commentRepository.countByPostPostId(post.getPostId());
              boolean likedByUser = likeRepository.existsByUserUserIdAndPost(userId, post);
              return postMapper.toGetPostResponse(
                  post, post.getLikeCount(), commentCount, likedByUser);
            })
        .toList();
  }

  // (도로명코드+건물본번) 별 게시글 인기순 조회 기능
  @Transactional
  public List<PostResponse> getAllPostsByRoadCodeBuildingNumberAndLikeCountDesc(
      String roadCode, String buildingNumber) {
    log.info("[PostService] (도로명코드+건물본번) 별 게시글 인기순 조회 시도");
    List<Post> postListLikeCount =
        postRepository.findAllByRoadCodeAndBuildingNumberOrderByLikeCountDesc(
            roadCode, buildingNumber);

    Long userId = 1L;
    return postListLikeCount.stream()
        .map(
            post -> {
              long commentCount = commentRepository.countByPostPostId(post.getPostId());
              boolean likedByUser = likeRepository.existsByUserUserIdAndPost(userId, post);
              return postMapper.toGetPostResponse(
                  post, post.getLikeCount(), commentCount, likedByUser);
            })
        .toList();
  }
}

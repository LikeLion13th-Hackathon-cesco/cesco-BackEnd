package com.practice.likelionhackathoncesco.domain.post.service;

import com.practice.likelionhackathoncesco.domain.community.entity.Community;
import com.practice.likelionhackathoncesco.domain.community.exception.CommunityErrorCode;
import com.practice.likelionhackathoncesco.domain.community.repository.CommunityRepository;
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
  
  private final CommunityRepository communityRepository;
  private final PostRepository postRepository;
  private final PostMapper postMapper;
  private final LikeRepository likeRepository;
  private final UserRepository userRepository;


  // 게시글 생성
  @Transactional
  public PostResponse createPost(CreatePostRequest createPostRequest) { // user는 인증객체 X , 그냥 고정된 더미데이터 쓸거라서 파라미터에 user 추가 X


    log.info("[PostService] 게시글 생성 시도 : userId={}, communityId={}, content={}", createPostRequest.getUserId(), createPostRequest.getCommunityId(), createPostRequest.getContent());

    if(createPostRequest.getContent() == null || createPostRequest.getContent().isBlank()){
      throw new CustomException(PostErrorCode.INVALID_POST_CONTENT);
    }
    Community community = communityRepository.findById(createPostRequest.getCommunityId()).orElseThrow(()-> new CustomException(
        CommunityErrorCode.COMMUNITY_NOT_FOUND));

    User user = userRepository.findById(createPostRequest.getUserId()).orElseThrow(()-> new CustomException(
        UserErrorCode.USER_NOT_FOUND));   // createPostRequest의 userId로 user 찾아서 builder로 post 객체 만들때 씀

    Post post = Post.builder()
        .content(createPostRequest.getContent())
        .user(user)
        .community(community)
        .build();

    postRepository.save(post);
    long likeCount = likeRepository.countByPost(post);  // toPostResponse에 좋아요 개수 계산해서 넣기 위해 -> 여기선 0

    log.info("[PostService] 게시글 생성 완료 : postId={}, userId={}, content={}", post.getPostId(), post.getUser().getUserId(), createPostRequest.getContent());

    return postMapper.toPostResponse(post, likeCount);
  }


  // 게시글 삭제 - 댓글 좋아요 같이 삭제 되야함 ^^^^^^^^^^^^^^^^^
  @Transactional
  public boolean deletePost(Long postId){
    log.info("[PostService] 게시글 삭제 시도 : postId = {}", postId);

    Post post = postRepository.findById(postId).orElseThrow(()-> new CustomException(PostErrorCode.POST_NOT_FOUND));

    postRepository.delete(post);
    log.info("[PostService] 게시글 삭제 완료 : postId = {}", postId);

    return true;
  }


  // 게시글 수정
  @Transactional
  public PostResponse updatePost(UpdatePostRequest updatePostRequest, Long postId) {
    log.info("[PostService] 후기 수정 시도 : postId={}, newContent={}",
        postId, updatePostRequest.getContent());

    if (updatePostRequest.getContent() == null || updatePostRequest.getContent().isBlank()) {
      throw new CustomException(PostErrorCode.INVALID_POST_CONTENT);
    }

    Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

    post.update(updatePostRequest.getContent());

    long likeCount = likeRepository.countByPost(post);  // toPostResponse에 좋아요 개수 계산해서 넣기 위해

    log.info("[PostService] 게시글 수정 완료 : postId={}, content = {}", postId, updatePostRequest.getContent());

    return postMapper.toPostResponse(post, likeCount);

  }

  // 게시글 단일 조회 기능
  @Transactional
  public PostResponse getPostById(Long postId){
    log.info("[PostService] 게시글 단일 조회 시도 : postId = {}", postId);
    Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));

    long likeCount = likeRepository.countByPost(post);  // toPostResponse에 좋아요 개수 계산해서 넣기 위해

    log.info("[PostService] 게시글 단일 조회 완료 : postId = {}", postId);

    return postMapper.toPostResponse(post, likeCount);
  }


  // 커뮤니티 별 게시글 전체 조회 기능
  @Transactional
  public List<PostResponse> getAllPostsByCommunityId(Long communityId) {
    log.info("[PostService] 커뮤니티 별 게시글 전체 조회 시도");
    List<Post> postList = postRepository.findAllByCommunityId(communityId);

    if(postList == null || postList.isEmpty()){
      throw new CustomException(PostErrorCode.POST_NOT_FOUND);
    }
    return postList.stream().map(post -> {
      long likeCount = likeRepository.countByPost(post);
      return postMapper.toPostResponse(post, likeCount);
    }).toList();
  }


  // 커뮤니티 별 게시글 최신순 조회 기능
  @Transactional
  public List<PostResponse> getAllPostsByCommunityIdAndCreatedAtDesc(Long communityId) {
    List<Post> postListCreatedAtDesc = postRepository.findAllByCommunityIdOrderByCreatedAtDesc(communityId);

    return postListCreatedAtDesc.stream().map(post -> {
      long likeCount = likeRepository.countByPost(post);
      return postMapper.toPostResponse(post, likeCount);
    }).toList();
  }


  // 커뮤니티 별 게시글 인기순 조회 기능
  @Transactional
  public List<PostResponse> getAllPostsByCommunityIdAndLikeCountDesc(Long communityId) {
    List<Post> postListLikeCount = postRepository.findAllByCommunityIdOrderByLikeCountDesc(communityId);

    return postListLikeCount.stream().map(post -> {
      long likeCount = likeRepository.countByPost(post);
      return postMapper.toPostResponse(post, likeCount);
    }).toList();
  }


}



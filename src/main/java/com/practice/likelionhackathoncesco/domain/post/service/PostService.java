package com.practice.likelionhackathoncesco.domain.post.service;

import com.practice.likelionhackathoncesco.domain.community.entity.Community;
import com.practice.likelionhackathoncesco.domain.community.exception.CommunityErrorCode;
import com.practice.likelionhackathoncesco.domain.community.repository.CommunityRepository;
import com.practice.likelionhackathoncesco.domain.post.dto.request.CreatePostRequest;
import com.practice.likelionhackathoncesco.domain.post.dto.request.UpdatePostRequest;
import com.practice.likelionhackathoncesco.domain.post.dto.response.PostResponse;
import com.practice.likelionhackathoncesco.domain.post.entity.Post;
import com.practice.likelionhackathoncesco.domain.post.exception.PostErrorCode;
import com.practice.likelionhackathoncesco.domain.post.mapper.PostMapper;
import com.practice.likelionhackathoncesco.domain.post.repository.PostRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

  // 사실 User user 에 따라서 게시글 생성, 삭제해야하는데 일단 제외함
  // 게시글 특정 개수 이상 생성 시 -> 크레딧 지급 추가 필요!
  
  private final CommunityRepository communityRepository;
  private final PostRepository postRepository;
  private final PostMapper postMapper;

  
  
  // like 도메인 만들어지고 나머지도 이부분 처럼 수정해야 함
  // 게시글 생성 - 고정 user에 따라 수정 필요 + 개수 증가 시 마다 크레딧 지급 여부 확인 필요
  @Transactional
  public PostResponse createPost(CreatePostRequest createPostRequest){

    log.info("[PostService] 게시글 생성 시도 : communityId = " + createPostRequest.getCommunityId() + ", content = " + createPostRequest.getContent());

    if(createPostRequest.getContent() == null || createPostRequest.getContent().isBlank()){
      throw new CustomException(PostErrorCode.INVALID_POST_CONTENT);
    }
    Community community = communityRepository.findById(createPostRequest.getCommunityId()).orElseThrow(()-> new CustomException(
        CommunityErrorCode.COMMUNITY_NOT_FOUND));

    Post post = Post.builder()
        .content(createPostRequest.getContent())
        .community(community)
        .build();

    postRepository.save(post);
    long likeCount = likeRepository.countByPost(post);


    log.info("[PostService] 게시글 생성 완료 : postId ="+post.getPostId()+", communityId = " + post.getCommunity().getCommunityId() + ", content = "+post.getContent());
    return postMapper.toPostResponse(post, likeCount);
  }

  // 게시글 삭제 - 고정 user에 따라 수정 필요 + 댓글 좋아요 같이 삭제 되야함 일단 보류
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
  public PostResponse updatePost(Long postId, UpdatePostRequest updatePostRequest) {
    log.info("[PostService] 후기 수정 시도 : postId={}, newContent={}",
        postId, updatePostRequest.getContent());

    if (updatePostRequest.getContent() == null || updatePostRequest.getContent().isBlank()) {
      throw new CustomException(PostErrorCode.INVALID_POST_CONTENT);
    }

    Post post = postRepository.findById(postId).orElseThrow(() -> {
      log.warn("[PostService] 게시글 수정 실패 - 존재하지 않음: postId={}", postId);
      return new IllegalArgumentException("게시글을 찾을 수 없습니다");
    });

    post.update(updatePostRequest.getContent());

    log.info("[PostService] 게시글 수정 완료 : postId={}, content = {}", postId, updatePostRequest.getContent());

    return postMapper.toPostResponse(post);

  }
  
  // 게시글 최신순 조회 기능
  public List<PostResponse> getAllPostsByCreatedAtDesc() {
    List<Post> postListCreatedAtDesc = postRepository.findAllByOrderByCreatedAtDesc();
    return postListCreatedAtDesc.stream().map(postMapper::toPostResponse).toList();
  }
  /*
  // 게시글 인기순 조회 기능
  public List<PostResponse> getAllPostsByLikeCountDesc() {
    log.info("[PostService] 인기 게시글 목록 조회 시도");
    List<Post> postList = postRepository.findAll();
    List<PostResponse> responseList = postList.stream().map(postMapper::toPostResponse)
  }
  */
  // 게시글 일정 기준 초과 시 -> 크레딧 지급
}



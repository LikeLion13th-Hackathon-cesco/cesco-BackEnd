package com.practice.likelionhackathoncesco.domain.like.controller;

import com.practice.likelionhackathoncesco.domain.like.dto.response.LikeResponse;
import com.practice.likelionhackathoncesco.domain.like.service.LikeService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "like", description = "좋아요 관련 API")
public class LikeController { // like처럼 request 가 없는 도메인은 RESTful API 경로 설정에 주의

  private final LikeService likeService;

  // 좋아요 생성 API
  @PostMapping("/likes/{userId}/{postId}") // like 도메인에 request DTO를 안만들어서 이럼
  @Operation(summary = "좋아요 생성", description = "특정 게시글에 좋아요를 눌렀을때 요청되는 API")
  public ResponseEntity<BaseResponse<LikeResponse>> createLike(
      @Parameter(description = "좋아요를 누르는 사용자 ID") @PathVariable Long userId,
      @Parameter(description = "좋아요를 누르는 게시글 ID") @PathVariable Long postId) {
    LikeResponse likeResponse = likeService.createLike(userId, postId);

    return ResponseEntity.ok(BaseResponse.success("좋아요 등록 완료", likeResponse));
  }

  // 좋아요 삭제 API
  @DeleteMapping("/likes/{userId}/{postId}") // like 도메인에 request DTO를 안만들어서 이럼
  @Operation(summary = "좋아요 삭제", description = "특정 게시글에 대해 이미 좋아요가 되어있는데 좋아요 버튼을 눌렀을때 요청되는 API")
  public ResponseEntity<BaseResponse<Boolean>> deleteLike(
      @Parameter(description = "좋아요를 삭제하는 사용자 ID") @PathVariable Long userId,
      @Parameter(description = "좋아요를 삭제하는 게시글 ID") @PathVariable Long postId) {

    likeService.deleteLike(userId, postId);

    return ResponseEntity.ok(BaseResponse.success("좋아요 취소 완료", true));
  }
}

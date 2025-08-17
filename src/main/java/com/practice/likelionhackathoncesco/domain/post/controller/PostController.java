package com.practice.likelionhackathoncesco.domain.post.controller;

import com.practice.likelionhackathoncesco.domain.post.dto.request.CreatePostRequest;
import com.practice.likelionhackathoncesco.domain.post.dto.request.UpdatePostRequest;
import com.practice.likelionhackathoncesco.domain.post.dto.response.PostResponse;
import com.practice.likelionhackathoncesco.domain.post.service.PostService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "post", description = "게시글 관련 API")
public class PostController {

  private final PostService postService;

  // 게시글 생성 API
  @PostMapping("/posts")
  @Operation(summary = "게시글 생성", description = "게시글 작성 후 완료버튼 눌렀을때 요청되는 API")
  public ResponseEntity<BaseResponse<PostResponse>> createPost(
      @Parameter(description = "게시글 작성 내용") @RequestBody @Valid
          CreatePostRequest createPostRequest) {
    PostResponse postResponse = postService.createPost(createPostRequest);

    return ResponseEntity.ok(BaseResponse.success("게시글 생성 성공", postResponse));
  }

  // 게시글 수정 API
  @PutMapping("/posts/{postId}")
  @Operation(summary = "게시글 수정", description = "게시글 수정 후 완료 버튼 눌렀을때 요청되는 API")
  public ResponseEntity<BaseResponse<PostResponse>> updatePost(
      @Parameter(description = "게시글 수정 내용") @RequestBody UpdatePostRequest updatePostRequest,
      @Parameter(description = "수정하는 게시글 ID") @PathVariable Long postId) {

    PostResponse postResponse = postService.updatePost(updatePostRequest, postId);
    return ResponseEntity.ok(BaseResponse.success("게시글 수정 완료", postResponse));
  }

  // 게시글 삭제 API
  @Operation(summary = "게시글 삭제", description = "게시글 삭제 버튼을 눌렀을때 요청되는 API")
  @DeleteMapping("/posts/{postId}")
  public ResponseEntity<BaseResponse<Boolean>> deletePost(
      @Parameter(description = "삭제할 게시글 ID") @PathVariable Long postId) {
    boolean isDeleted = postService.deletePost(postId);
    return ResponseEntity.ok(BaseResponse.success("게시글 삭제 성공", isDeleted));
  }

  // 게시글 단일 조회 API
  @Operation(summary = "게시글 단일 조회", description = "게시글 목록에서 특정 게시글를 눌렀을때 호출되는 API")
  @GetMapping("/posts/{postId}")
  public ResponseEntity<BaseResponse<PostResponse>> getPost(
      @Parameter(description = "조회할 게시글 ID") @PathVariable Long postId) {
    PostResponse postResponse = postService.getPostById(postId);
    return ResponseEntity.ok(BaseResponse.success("게시글 단일 조회 성공", postResponse));
  }

  // (도로명코드+건물본번) 별 게시글 전체 조회 API
  @Operation(
      summary = "(도로명코드+건물본번) 별 게시글 전체 조회",
      description = "(도로명코드+건물본번) 페이지에서 특정 주소를 검색완료 했을때 호출되는 API")
  @GetMapping("/posts/{roadCode}/{buildingNumber}")
  public ResponseEntity<BaseResponse<List<PostResponse>>> getPostByRoadCodeAndBuildingNumber(
      @Parameter(description = "조회할 게시글 목록의 도로명코드") @PathVariable String roadCode,
      @Parameter(description = "조회할 게시글 목록의 건물본번") @PathVariable String buildingNumber) {
    List<PostResponse> postList =
        postService.getAllPostsByRoadCodeAndBuildingNumber(roadCode, buildingNumber);
    return ResponseEntity.ok(BaseResponse.success("(도로명코드+건물본번) 별 게시글 목록 조회 완료", postList));
  }

  // (도로명코드+건물본번) 별 게시글 최신순으로 조회 API
  @Operation(
      summary = "(도로명코드+건물본번) 별 게시글 최신순 조회",
      description = "(도로명코드+건물본번) 별 게시글 목록에서 최신순 버튼을 눌렀을때 호출되는 API")
  @GetMapping("/posts/{roadCode}/{buildingNumber}/latest")
  public ResponseEntity<BaseResponse<List<PostResponse>>>
      getPostByRoadCodeAndBuildingNumberAndLatest(
          @Parameter(description = "최신순으로 조회할 게시글 목록의 도로명코드") @PathVariable String roadCode,
          @Parameter(description = "최신순으로 조회할 게시글 목록의 건물본번") @PathVariable String buildingNumber) {
    List<PostResponse> postList =
        postService.getAllPostsByRoadCodeAndBuildingNumberAndCreatedAtDesc(
            roadCode, buildingNumber);
    return ResponseEntity.ok(BaseResponse.success("(도로명코드+건물본번) 별 게시글 최신순 조회 완료", postList));
  }

  // (도로명코드+건물본번) 별 게시글 인기순으로 조회 API
  @Operation(
      summary = "(도로명코드+건물본번) 별 게시글 좋아요 많은순 조회",
      description = "(도로명코드+건물본번) 별 게시글 목록에서 인기순 버튼을 눌렀을때 호출되는 API")
  @GetMapping("/posts/{roadCode}/{buildingNumber}/popular")
  public ResponseEntity<BaseResponse<List<PostResponse>>>
      getPostByRoadCodeAndBuildingNumberAndPopular(
          @Parameter(description = "인기순으로 조회할 게시글 목록의 도로명코드") @PathVariable String roadCode,
          @Parameter(description = "인기순으로 조회할 게시글 목록의 건물본번") @PathVariable String buildingNumber) {
    List<PostResponse> postList =
        postService.getAllPostsByRoadCodeBuildingNumberAndLikeCountDesc(roadCode, buildingNumber);
    return ResponseEntity.ok(BaseResponse.success("(도로명코드+건물본번) 별 게시글 인기순 조회 완료", postList));
  }
}

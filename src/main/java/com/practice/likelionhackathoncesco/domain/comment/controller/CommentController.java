package com.practice.likelionhackathoncesco.domain.comment.controller;

import com.practice.likelionhackathoncesco.domain.comment.dto.request.CreateCommentRequest;
import com.practice.likelionhackathoncesco.domain.comment.dto.request.UpdateCommentRequest;
import com.practice.likelionhackathoncesco.domain.comment.dto.response.CommentResponse;
import com.practice.likelionhackathoncesco.domain.comment.service.CommentService;
import com.practice.likelionhackathoncesco.domain.post.dto.request.CreatePostRequest;
import com.practice.likelionhackathoncesco.domain.post.dto.request.UpdatePostRequest;
import com.practice.likelionhackathoncesco.domain.post.dto.response.PostResponse;
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
@Tag(name = "comment", description = "댓글 관련 API")
public class CommentController {


  private final CommentService commentService;

  // 댓글 생성 API
  @PostMapping("/comments")
  @Operation(summary = "댓글 생성", description = "댓글 작성 후 완료버튼 눌렀을때 요청되는 API")
  public ResponseEntity<BaseResponse<CommentResponse>> createComment(
      @Parameter(description = "댓글 작성 내용")
      @RequestBody @Valid CreateCommentRequest createCommentRequest) {
    CommentResponse commentResponse = commentService.createComment(createCommentRequest);

    return ResponseEntity.ok(BaseResponse.success("댓글 생성 성공", commentResponse));
  }

  // 댓글 수정 API
  @PutMapping("/comments/{commentId}")
  @Operation(summary = "댓글 수정", description = "댓글 수정 후 완료 버튼 눌렀을때 요청되는 API")
  public ResponseEntity<BaseResponse<CommentResponse>> updateComment(
      @Parameter(description = "댓글 수정 내용") @RequestBody UpdateCommentRequest updateCommentRequest,
      @Parameter(description = "수정하는 댓글 ID") @PathVariable Long commentId) {

    CommentResponse commentResponse = commentService.updateComment(updateCommentRequest, commentId);
    return ResponseEntity.ok(BaseResponse.success("댓글 수정 완료", commentResponse));
  }

  // 댓글 삭제 API
  @Operation(summary = "댓글 삭제", description = "댓글 삭제 버튼을 눌렀을때 요청되는 API")
  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<BaseResponse<Boolean>> deleteComment(
      @Parameter(description = "삭제할 댓글 ID") @PathVariable Long commentId) {
    boolean isDeleted = commentService.deleteComment(commentId);
    return ResponseEntity.ok(BaseResponse.success("댓글 삭제 성공", isDeleted));
  }

  // 게시글 별 댓글 전체 조회 API
  @Operation(summary = "게시글 별 게시글 전체 조회", description = "특정 게시글을 눌렀을때 호출되는 API")
  @GetMapping("/comments/{postId}")
  public ResponseEntity<BaseResponse<List<CommentResponse>>> getAllCommentByPostId(
      @Parameter(description = "조회할 댓글 목록의 게시글 ID") @PathVariable Long postId){
    List<CommentResponse> commentList = commentService.getAllCommentsByPostId(postId);
    return ResponseEntity.ok(BaseResponse.success("커뮤니티 별 게시글 목록 조회 완료", commentList));
  }
}

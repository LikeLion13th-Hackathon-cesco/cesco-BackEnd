package com.practice.likelionhackathoncesco.domain.user.controller;

import com.practice.likelionhackathoncesco.domain.analysisreport.service.AnalysisReportService;
import com.practice.likelionhackathoncesco.domain.user.dto.response.MyPageResponse;
import com.practice.likelionhackathoncesco.domain.user.dto.response.PayResponse;
import com.practice.likelionhackathoncesco.domain.user.service.UserPayService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

  private final UserPayService userPayService;
  private final AnalysisReportService analysisReportService;

  @Operation(summary = "Plus 요금제 결제 API", description = "Plus 요금제의 결제 버튼을 누르면 결제 처리 되는 API")
  @PostMapping("/{userId}")
  public ResponseEntity<BaseResponse<PayResponse>> processPayment(@PathVariable Long userId) {
    PayResponse result = userPayService.completePayment(userId);
    return ResponseEntity.ok(BaseResponse.success("결제 완료", result));
  }

  @Operation(summary = "마이페이지 데이터 조회 API", description = "특정 사용자의 크레딧과 분석 리포트 목록을 조회")
  @GetMapping("/{userId}")
  public ResponseEntity<BaseResponse<MyPageResponse>> getMyPageData(
      @Parameter(description = "사용자 ID") @PathVariable Long userId) {

    log.info("마이페이지 데이터 조회 요청: userId={}", userId);

    MyPageResponse result = analysisReportService.getAllMyPageReport(userId);

    return ResponseEntity.ok(BaseResponse.success("마이페이지 데이터 조회 성공", result));
  }
}

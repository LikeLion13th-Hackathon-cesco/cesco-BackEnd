package com.practice.likelionhackathoncesco.domain.fraudreport.dto.response;

import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FraudRegisterResponse { // 이미지 url 반환 필요 없음

  @Schema(description = "업로드한 사기 등기부등본 Id")
  private Long fraudRegisterReportId; // 사기 등기부등본 고유 번호

  @Schema(description = "업로드한 파일 이름", example = "등기부등본.pdf")
  private String fileName; // 원본 파일이름

  @Schema(description = "신고 상태")
  private ReportStatus reportStatus;
}

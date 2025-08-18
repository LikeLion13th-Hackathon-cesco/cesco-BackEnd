package com.practice.likelionhackathoncesco.domain.fraudreport.dto.response;

import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FraudResponse {

  @Schema(description = "업로드한 고소장 id", example = "1")
  private Long complaintReportId;

  @Schema(description = "업로드한 고소장 원본 파일 이름", example = "고소장.pdf")
  private String complaintReportFileName;

  @Schema(description = "업로드한 신고 등기부등본 id", example = "1")
  private Long fraudReportId;

  @Schema(description = "업로드된 신고할 등기부등본 원본 파일 이름", example = "등기부등본.pdf")
  private String fraudReportFileName;

  @Schema(description = "신고 상태")
  private ReportStatus reportStatus;
}

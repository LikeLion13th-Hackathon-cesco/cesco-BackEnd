package com.practice.likelionhackathoncesco.domain.analysisreport.dto.response;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.Comment;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.Warning;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "AnalysisReportResponse DTO", description = "분석리포트 응답")
public class AnalysisReportResponse {

  @Schema(description = "분석 레포트 id", example = "1")
  private Long reportId;

  @Schema(description = "처리 상태", example = "위험도 분석중")
  private ProcessingStatus processingStatus;
}

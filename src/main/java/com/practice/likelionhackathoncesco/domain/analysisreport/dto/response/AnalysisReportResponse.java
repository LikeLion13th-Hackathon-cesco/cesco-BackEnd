package com.practice.likelionhackathoncesco.domain.analysisreport.dto.response;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.Comment;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "AnalysisReportResponse DTO", description = "분석리포트 응답")
public class AnalysisReportResponse {

  @Schema(description = "분석 레포트 id", example = "1")
  private Long reportId;

  @Schema(description = "등기부등본 파일 url", example = "...")
  private String analysisReportUrl;

  @Schema(description = "해당 부동산의 주소", example = "서울시 송파구 송파대로 48길 29")
  private String address;

  @Schema(description = "전월세 안전지수", example = "7.5")
  private Double safetyScore;

  @Schema(description = "분석 한줄 설명", example = "이 부동산은 거래에 주의가 필요합니다.")
  private String summary;

  @Schema(description = "안전 점수에 따른 한줄 평가", example = "해당 부동산은 비교적 안전합니다")
  private Comment comment;

  @Schema(description = "안전지수 분석 설명", example = "현재 등기부등본 갑구에는 말소되지 않은 가압류 1건과 압류 1건이 존재합니다.")
  private String safetyDescription;

  @Schema(description = "보증보험 가입 가능 여부 분석", example = "보증보험 심사에서 부정적으로 평가될 수 있어요.")
  private String insuranceDescription;

  @Schema(description = "처리 상태", example = "위험도 분석중")
  private ProcessingStatus processingStatus;
}

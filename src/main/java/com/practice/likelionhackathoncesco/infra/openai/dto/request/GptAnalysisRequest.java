package com.practice.likelionhackathoncesco.infra.openai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(title = "GptAnalysisRequestDTO", description = "텍스트 분석 요청 DTO")
public class GptAnalysisRequest {

  @Schema(
      description = "업로드된 파일 (PDF 형식)",
      type = "string",
      format = "binary",
      example = "파일을 선택하세요")
  private MultipartFile file; // 사용자가 업로드한 파일

  /*@Schema(description = "파일명", example = "등기부등본.pdf")
  private String fileName; // 파일명*/

  @Schema(description = "전월세 여부", example = "0")
  private Integer isMonthlyRent; // 월세일 경우 : 1 , 전세일 경우 : 0

  @Schema(description = "전월세 보증금", example = "100000000") // 1억
  private Integer deposit; // 전월세 보증금

  @Schema(description = "월세", example = "3000000")
  private Integer monthlyRent; // 전세인 경우 0으로 설정 필요^^^^^^^^^^

  @Schema(description = "상세 주소", example = "102동 201호")
  private String detailAddress;

  @Schema(description = "예시 파일 여부", example = "0/1/2/3")
  private Integer isExample;
}

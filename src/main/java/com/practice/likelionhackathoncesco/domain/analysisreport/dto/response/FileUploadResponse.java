package com.practice.likelionhackathoncesco.domain.analysisreport.dto.response;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResponse {

  // 파일 업로드를 하면 사용자에게 업로드한 파일 이름과 파일 업로드 성공 여부만 보여짐

  @Schema(description = "업로드한 파일 Id", example = "1")
  private Long reportId;

  @Schema(description = "업로드한 파일 이름", example = "등기부등본.pdf")
  private String fileName;

  @Schema(description = "진행 상태(파일 업로드 성공)")
  private ProcessingStatus processingStatus;

}

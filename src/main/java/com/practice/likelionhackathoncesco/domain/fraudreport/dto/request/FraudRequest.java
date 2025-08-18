package com.practice.likelionhackathoncesco.domain.fraudreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class FraudRequest {

  @Schema(
      description = "업로드된 고소장 파일 (PDF 형식)",
      type = "string",
      format = "binary",
      example = "파일을 선택하세요")
  private MultipartFile complaintFile;

  @Schema(
      description = "업로드된 신고할 등기부등본 파일 (PDF 형식)",
      type = "string",
      format = "binary",
      example = "파일을 선택하세요")
  private MultipartFile fraudReportFile;
}

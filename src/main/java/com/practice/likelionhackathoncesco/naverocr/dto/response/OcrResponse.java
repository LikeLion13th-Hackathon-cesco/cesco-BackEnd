package com.practice.likelionhackathoncesco.naverocr.dto.response;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OcrResponse {

  @Schema(description = "업로드한 파일의 s3key", example = "propertyregistry/uuid.pdf")
  private String s3Key;

  @Schema(description = "추출된 텍스트", example = ".")
  private String ocrText;

  @Schema(description = "감지된 텍스트", example = "가압류, 가등기")
  private String detectedKeywords;

  @Schema(description = "진행 상태", example = "OCR_COMPLETED")
  private ProcessingStatus processingStatus;

}

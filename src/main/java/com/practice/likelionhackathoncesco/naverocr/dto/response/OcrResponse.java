package com.practice.likelionhackathoncesco.naverocr.dto.response;

import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OcrResponse {

  @Schema(description = "추출된 텍스트", example = ".")
  private Map<String, List<String>> sections;


  @Schema(description = "진행 상태", example = "OCR_COMPLETED")
  private ProcessingStatus processingStatus;

}

package com.practice.likelionhackathoncesco.naverocr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.practice.likelionhackathoncesco.naverocr.dto.ImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OcrRequest {

  @Schema(description = "요청 버전", example = "v2")
  @JsonProperty("version")
  private String version;

  @Schema(description = "ocr API로 보낼 요청 고유 id", example = "...")
  @JsonProperty("requestId")
  private String requestId; // 고유한 요청 ID

  @Schema(description = "ocr API로 보낸 요청 시간", example = "...")
  @JsonProperty("timestamp")
  private Long timestamp; // 요청 시간

  @Schema(description = "파일 언어", example = "ko")
  @JsonProperty("lang")
  private String lang = "ko"; // 한국어로 고정

  @JsonProperty("enableTableDetection")
  private Boolean enableTableDetection = true; // 테이블 감지 여부 (표 인식 여부 -> 등기부등본 파싱을 위해 ture로 설정)

  @Schema(description = "ocr API로 보낼 요청 고유 id", example = "...")
  @JsonProperty("images")
  private List<ImageDto> images; // 처리할 이미지 목록

}

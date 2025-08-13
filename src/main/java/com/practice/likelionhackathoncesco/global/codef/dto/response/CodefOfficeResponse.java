package com.practice.likelionhackathoncesco.global.codef.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CodefOfficeResponse {

  @Schema(description = "금액", example = "2113000")
  @JsonProperty("resAmount")
  private String resAmount;

  @Schema(description = "면적", example = "28.840")
  @JsonProperty("resArea")
  private String resArea;

  @Schema(description = "일자", example = "20200101")
  @JsonProperty("resReportingDate")
  private String resReportingDate;

}

package com.practice.likelionhackathoncesco.global.codef.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CodefResponse {

  @Schema(description = "주소", example = "[열람지역]")
  @JsonProperty("resUserAddr")
  private String resUserAddr;

  @Schema(description = "가격 List", example = "<공동주택공시가격>")
  @JsonProperty("resPriceList")
  private List<Object> resPriceList; // 가격 리스트
}

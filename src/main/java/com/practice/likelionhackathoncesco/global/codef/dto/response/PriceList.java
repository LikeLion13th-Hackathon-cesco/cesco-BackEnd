package com.practice.likelionhackathoncesco.global.codef.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PriceList {

  @Schema(description = "기준일자", example = "[공시기준], (YYYYMMDD)")
  @JsonProperty("resReferenceDate")
  private String resReferenceDate; // 기준일자 (YYYYMMDD)

  @Schema(description = "단지명", example = ".")
  @JsonProperty("resComplexName")
  private String resComplexName; // 단지명

  @Schema(description = "동", example = ".")
  @JsonProperty("resAddrDong")
  private String resAddrDong; // 동

  @Schema(description = "호", example = ".")
  @JsonProperty("resAddrHo")
  private String resAddrHo; // 호

  @Schema(description = "면적", example = "[전용면적], (단위: m²), (ex. \"84.71\")")
  @JsonProperty("resArea")
  private String resArea; // 면적 (전용면적, m²)

  @Schema(description = "기준가격", example = "[공동주택가격]")
  @JsonProperty("resBasePrice")
  private String resBasePrice; // 기준가격 (공동주택가격)
}

package com.practice.likelionhackathoncesco.global.codef.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CodefRequest {

  @Schema(description = "기관 코드", example = "0004(고정값)")
  @JsonProperty("organization")
  private String organization;

  @Schema(description = "주소 검색 구분", example = "'0':지번검색 / '1':도로명검색")
  @JsonProperty("inquiryType")
  private String inquiryType;

  @Schema(description = "주소_도로명", example = "서초대로")
  @JsonProperty("addrRoadName")
  private String addrRoadName;

  @Schema(description = "주소_건물번호(도로명)", example = "56")
  @JsonProperty("addrBuildingNumber")
  private String addrBuildingNumber;

  @Schema(description = "주소_시/도", example = "11(서울특별시)")
  @JsonProperty("addrSido")
  private String addrSido;

  @Schema(description = "주소_시군구", example = "11650(서초구)")
  @JsonProperty("addrSigungu")
  private String addrSigungu;

  @Schema(description = "층", example = "2층")
  @JsonProperty("floor")
  private String floor;

  @Schema(description = "동", example = "101동")
  @JsonProperty("dong")
  private String dong;

  @Schema(description = "호", example = "201호")
  @JsonProperty("ho")
  private String ho;

}

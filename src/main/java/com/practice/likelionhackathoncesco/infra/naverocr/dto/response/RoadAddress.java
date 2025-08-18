package com.practice.likelionhackathoncesco.infra.naverocr.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RoadAddress {

  @Schema(description = "시도", example = "서울특별시")
  private String sido;

  @Schema(description = "시군구", example = "서초구")
  private String sigungu;

  @Schema(description = "도로명", example = "서초대로")
  private String roadName;

  @Schema(description = "건물번호", example = "219")
  private String buildingNumber;
}

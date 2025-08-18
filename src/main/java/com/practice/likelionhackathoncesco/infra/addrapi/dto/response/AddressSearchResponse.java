package com.practice.likelionhackathoncesco.infra.addrapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
@Schema(title = "addressSearchResponseDTO", description = "도로명주소 정보 조회 응답 반환")
public class AddressSearchResponse {

  @Schema(description = "검색 목록에 띄울 도로명주소", example = "서울특별시 송파구 송파대로48길 29")
  private String roadAddrPart1;   // 카카오맵에서 쓰고, 예상 검색 목록에 띄우기 위한 도로명주소

  @Schema(description = "도로명코드", example = "117104169350")
  private String rnMgtSn;   // 도로명코드

  @Schema(description = "건물본번", example = "29")
  private String buldMnnm;  // 건물본번
}

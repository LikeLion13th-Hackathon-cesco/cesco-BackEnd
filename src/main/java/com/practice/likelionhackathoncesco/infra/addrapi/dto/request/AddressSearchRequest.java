package com.practice.likelionhackathoncesco.infra.addrapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(title = "addressSearchRequestDTO", description = "검색어에 따른 도로명주소 정보 조회 요청")
public class AddressSearchRequest {

  @Schema(description = "현재 페이지 정보", example = "1")
  private Number currentPage; // 현재 페이지 번호

  @Schema(description = "페이지당 출력할 결과 개수", example = "100")
  private Number countPerPage; // 페이지당 출력할 결과 (100까지 가능)

  @Schema(description = "사용자 주소 검색어", example = "송파대로48길")
  private String keyword; // 사용자 주소 검색어

  @Schema(description = "응답 반환 형식", example = "json")
  private String resultType; // json 이면 JSON형식의 결과 제공
}

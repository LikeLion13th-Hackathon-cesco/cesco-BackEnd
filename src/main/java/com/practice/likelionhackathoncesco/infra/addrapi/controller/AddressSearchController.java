package com.practice.likelionhackathoncesco.infra.addrapi.controller;

import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import com.practice.likelionhackathoncesco.infra.addrapi.dto.request.AddressSearchRequest;
import com.practice.likelionhackathoncesco.infra.addrapi.dto.response.AddressSearchResponse;
import com.practice.likelionhackathoncesco.infra.addrapi.service.AddressSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "search community", description = "커뮤니티 주소 검색 관련 API")
public class AddressSearchController {
  private final AddressSearchService addressSearchService;

  @Operation(summary = "커뮤니티 주소 검색 결과 반환 API", description = "커뮤니티 페이지에서 검색 했을때 검색 결과 반환")
  @GetMapping("/address-search")
  public ResponseEntity<BaseResponse<List<AddressSearchResponse>>> searchCommunity(
      @RequestBody AddressSearchRequest addressSearchRequest) {

    List<AddressSearchResponse> addressSearchResponseList =
        addressSearchService.searchAddress(addressSearchRequest);
    return ResponseEntity.ok(BaseResponse.success("커뮤니티 주소 검색 완료", addressSearchResponseList));
  }
}

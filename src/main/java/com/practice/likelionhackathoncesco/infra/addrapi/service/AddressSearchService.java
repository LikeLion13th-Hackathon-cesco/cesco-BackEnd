package com.practice.likelionhackathoncesco.infra.addrapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.likelionhackathoncesco.global.config.AddrApiConfig;
import com.practice.likelionhackathoncesco.infra.addrapi.dto.request.AddressSearchRequest;
import com.practice.likelionhackathoncesco.infra.addrapi.dto.response.AddressSearchResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressSearchService {

  private final String API_URL = "{ADDR_URL}";
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final AddrApiConfig addrApiConfig;

  // API 에게 요청하고 응답받는 메소드
  public List<AddressSearchResponse> searchAddress(AddressSearchRequest addressSearchRequest) {

    log.info("[AddressSearchService] 주소 검색 시도 : keyword = {}", addressSearchRequest.getKeyword());

    String encodedKeyword =
        URLEncoder.encode(addressSearchRequest.getKeyword(), StandardCharsets.UTF_8);

    // 기본 요청 값 지정
    if (addrApiConfig.getConfmKey() == null || addrApiConfig.getConfmKey().isEmpty()) {
      addrApiConfig.setConfmKey("{ADDR_KEY}");
    }
    if (addressSearchRequest.getCurrentPage() == null) {
      addressSearchRequest.setCurrentPage(1);
    }
    if (addressSearchRequest.getCountPerPage() == null) {
      addressSearchRequest.setCountPerPage(10);
    }
    if (addressSearchRequest.getResultType() == null
        || addressSearchRequest.getResultType().isEmpty()) {
      addressSearchRequest.setResultType("json");
    }

    try {
      String params =
          String.format(
              "confmKey=%s&currentPage=%d&countPerPage=%d&keyword=%s&resultType=%s",
              addrApiConfig.getConfmKey(),
              addressSearchRequest.getCurrentPage().intValue(), // Number형 %d 로 하면 오류 날 수 있음
              addressSearchRequest.getCountPerPage().intValue(),
              encodedKeyword,
              addressSearchRequest.getResultType());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      HttpEntity<String> requestEntity = new HttpEntity<>(params, headers);

      log.info("[AddressSearchService] API 응답 수신");

      String jsonResponse = restTemplate.postForObject(API_URL, requestEntity, String.class);

      log.info("API 응답 원본: {}", jsonResponse); // 원본 응답 로그

      // JSON 배열 형태로 파싱
      JsonNode root = objectMapper.readTree(jsonResponse);
      JsonNode addrResults = root.get("results").path("juso");

      List<AddressSearchResponse> addressSearchResponses = new ArrayList<>();
      if (addrResults.isArray()) {
        for (JsonNode node : addrResults) {
          AddressSearchResponse addressSearchResponse =
              AddressSearchResponse.builder()
                  .roadAddrPart1(node.path("roadAddrPart1").asText())
                  .rnMgtSn(node.path("rnMgtSn").asText())
                  .buldMnnm(node.path("buldMnnm").asText())
                  .build();
          addressSearchResponses.add(addressSearchResponse);
        }
        log.info("[AddressSearchService] 검색 결과 {}건 추출", addressSearchResponses.size());
      } else {
        log.warn("[AddressSearchService] 검색 결과 없음");
      }
      return addressSearchResponses; // JSON 배열 형태로 반환
    } catch (Exception e) {
      log.error("[AddressSearchService] 주소 검색 중 오류 발생", e);
      return Collections.emptyList();
    }
  }
}

package com.practice.likelionhackathoncesco.global.codef.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.S3ErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.global.codef.dto.request.CodefRequest;
import com.practice.likelionhackathoncesco.global.codef.dto.response.CodefResponse;
import com.practice.likelionhackathoncesco.global.codef.dto.response.PriceList;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallCodefApi {

  private final AccessTokenService accessTokenService;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper; // json 파싱용

  private final AnalysisReportRepository analysisReportRepository;

  @Value("${CODEF_ENDPOINT}")
  private String url;

  public CodefResponse extractPrice(Long reportId) {
    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    try {
      log.info("공동주택 공시가격 추출 시작");

      // Codef API 요청 생성
      CodefRequest codefRequest = createCodefRequest();

      // Codef API 호출
      CodefResponse codefResult = callCodefApi(codefRequest);

      log.info("공시가격 추출 처리 완료: reportId={}", reportId);

      return CodefResponse.builder()
          .resUserAddr(codefResult.getResUserAddr())
          .resPriceList(codefResult.getResPriceList())
          .build();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  // CODEF API 호출하여 파싱된 데이터 반환
  public CodefResponse callCodefApi(CodefRequest codefRequest) throws IOException {

    // 생성한 엑세스를 헤더에 포함하기 위하여 저장
    String accessToken = accessTokenService.getValidToken();

    try{
      // HTTP 헤더 설정(엑세스 토큰 값을 넣어서 요청)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(accessToken); // 엑세스 토큰을 헤더에 포함

      // API 요청 엔티티 생성
      HttpEntity<CodefRequest> requestEntity = new HttpEntity<>(codefRequest, headers);

      log.info("CODEF API 호출 시작");

      ResponseEntity<String> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          String.class
      );

      return parseResponse(response);

    } catch (Exception e) {
      log.error("CODEF API 호출 실패", e);
      throw new IOException("CODEF API 호출 실패", e);
    }
  }

  // 요청 생성
  public CodefRequest createCodefRequest() {
    return CodefRequest.builder()
        .organization("0007") // 기관 코드
        .addrSearchType("1") // 도로명 주소 검색
        .addrSido("서울특별시") // 서울 특별시
        .addrSiGunGu("은평구") // 서초구
        .addrRoadName("진관4로") // 도로명 주소 파싱해서 넣어야 함(우선 더미 정보대로 고정값으로 넣어봄)
        .addrBuildingNumber("100")
        .build();
  }

  // 응답 데이터 파싱
  public CodefResponse parseResponse(ResponseEntity<String> response) throws  IOException{
    try {
      String responseBody = response.getBody();
      if (responseBody == null || responseBody.trim().isEmpty()) {
        log.warn("응답 본문이 비어있습니다.");
        throw new IOException("응답 본문이 비어있습니다.");
      }

      log.info("파싱할 응답 본문: {}", responseBody);

      // URL 디코딩 추가
      String decodedResponse = java.net.URLDecoder.decode(responseBody, "UTF-8");
      log.info("디코딩된 응답 본문: {}", decodedResponse);

      // JSON 파싱
      JsonNode rootNode = objectMapper.readTree(responseBody);

      // 필수 응답 필드 추출
      String resUserAddr = extractStringValue(rootNode, "resUserAddr");

      // resPriceList 파싱 (Object 리스트로)
      List<Object> resPriceList = parseResPriceList(rootNode);

      log.info("파싱 완료 - 주소: {}, 가격리스트 개수: {}", resUserAddr, resPriceList.size());

      return CodefResponse.builder()
          .resUserAddr(resUserAddr)
          .resPriceList(resPriceList)
          .build();

    } catch (Exception e) {
      log.error("응답 파싱 실패", e);
      throw new IOException("응답 파싱 실패", e);
    }
  }

  // JsonNode에서 안전하게 문자열 값 추출
  private String extractStringValue(JsonNode node, String fieldName) {
    if (node == null) {
      return "";
    }

    JsonNode fieldNode = node.get(fieldName);
    if (fieldNode == null || fieldNode.isNull()) {
      return "";
    }

    return fieldNode.asText();
  }

  // resPriceList 파싱 - PriceList 객체들을 Object 리스트로 변환
  private List<Object> parseResPriceList(JsonNode rootNode) {
    List<Object> priceList = new ArrayList<>();

    try {
      JsonNode priceListNode = rootNode.get("resPriceList");

      if (priceListNode != null && !priceListNode.isNull()) {
        if (priceListNode.isArray()) {
          // 배열인 경우
          for (JsonNode priceNode : priceListNode) {
            PriceList priceListItem = createPriceListFromNode(priceNode);
            priceList.add(priceListItem);
          }
          log.info("배열 형태의 resPriceList 파싱 완료: {}건", priceList.size());
        } else {
          // 단일 객체인 경우
          PriceList priceListItem = createPriceListFromNode(priceListNode);
          priceList.add(priceListItem);
          log.info("단일 객체 형태의 resPriceList 파싱 완료");
        }
      } else {
        // resPriceList가 없는 경우, 루트에서 직접 PriceList 필드들을 찾아서 생성
        log.info("resPriceList 필드가 없어서 루트에서 직접 파싱 시도");
        PriceList priceListItem = createPriceListFromNode(rootNode);

        // 하나라도 값이 있으면 추가
        if (hasValidPriceListData(priceListItem)) {
          priceList.add(priceListItem);
          log.info("루트에서 PriceList 파싱 완료");
        } else {
          log.warn("유효한 PriceList 데이터를 찾을 수 없습니다.");
        }
      }

    } catch (Exception e) {
      log.error("resPriceList 파싱 중 오류", e);
    }

    return priceList;
  }

  // JsonNode에서 PriceList 객체 생성
  private PriceList createPriceListFromNode(JsonNode node) {
    return PriceList.builder()
        .resReferenceDate(extractStringValue(node, "resReferenceDate"))
        .resComplexName(extractStringValue(node, "resComplexName"))
        .resAddrDong(extractStringValue(node, "resAddrDong"))
        .resAddrHo(extractStringValue(node, "resAddrHo"))
        .resArea(extractStringValue(node, "resArea"))
        .resBasePrice(extractStringValue(node, "resBasePrice"))
        .build();
  }

  // PriceList에 유효한 데이터가 있는지 확인
  private boolean hasValidPriceListData(PriceList priceList) {
    return priceList != null && (
        !isEmpty(priceList.getResComplexName()) ||
            !isEmpty(priceList.getResBasePrice()) ||
            !isEmpty(priceList.getResArea()) ||
            !isEmpty(priceList.getResAddrDong()) ||
            !isEmpty(priceList.getResAddrHo()) ||
            !isEmpty(priceList.getResReferenceDate())
    );
  }

  private boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }


}

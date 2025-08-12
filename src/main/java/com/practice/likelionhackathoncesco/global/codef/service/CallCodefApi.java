package com.practice.likelionhackathoncesco.global.codef.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.S3ErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.global.codef.dto.request.CodefRequest;
import com.practice.likelionhackathoncesco.global.codef.dto.response.CodefOfficeResponse;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

  // api 호출하여 공시가격 파싱
  public CodefOfficeResponse extractPrice(Long reportId) {

    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    try {
      log.info("공동주택 공시가격 추출 시작");

      // Codef API 요청 생성
      CodefRequest codefRequest = createCodefRequest();

      // Codef API 호출
      CodefOfficeResponse codefResult = callCodefApi(codefRequest);

      log.info("공시가격 추출 처리 완료: reportId={}", reportId);

      return CodefOfficeResponse.builder()
          .resAmount(codefResult.getResAmount())
          .build();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  // CODEF API 호출하여 파싱된 데이터 반환
  public CodefOfficeResponse callCodefApi(CodefRequest codefRequest) throws IOException {

    // 엑세스 토큰을 생성한 후 헤더에 포함하기 위하여 저장
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
        .organization("0004") // 기관 코드
        .inquiryType("1")
        .addrSido("서울특별시") // 서울 특별시
        .addrSigungu("성북구") // 서초구
        .addrRoadName("동소문로5가") // 도로명 주소 파싱해서 넣어야 함(우선 더미 정보대로 고정값으로 넣어봄)
        .addrBuildingNumber("30")
        .floor("2")
        .dong("")
        .ho("201")
        .build();
  }

  public CodefOfficeResponse parseResponse(ResponseEntity<String> response) {

    try {
      // 응답 상태 코드 확인
      if (!response.getStatusCode().is2xxSuccessful()) {
        log.error("API 호출 실패 - 상태 코드: {}", response.getStatusCode());
        throw new IOException("API 호출 실패: " + response.getStatusCode());
      }

      String responseBody = response.getBody();

      // 응답 내용 검증
      if (responseBody == null || responseBody.trim().isEmpty()) {
        log.error("응답 내용이 비어있음");
        throw new IOException("응답 내용이 비어있습니다");
      }

      log.info("CODEF API 응답: {}", responseBody);

      // URL 디코딩 처리
      String decodedResponse;
      if (responseBody.startsWith("%")) {
        decodedResponse = URLDecoder.decode(responseBody, StandardCharsets.UTF_8);
        log.info("URL 디코딩된 응답: {}", decodedResponse);
      } else {
        decodedResponse = responseBody;
      }

      // JSON 파싱
      CodefOfficeResponse codefResponse = objectMapper.readValue(decodedResponse, CodefOfficeResponse.class);

      log.info("JSON 파싱 성공");
      return codefResponse;

    } catch (JsonProcessingException e) {
      log.error("JSON 파싱 실패: {}", e.getMessage());
      throw new RuntimeException("응답 데이터 파싱 실패", e);
    } catch (IOException e) {
      log.error("응답 처리 실패: {}", e.getMessage());
      throw new RuntimeException("응답 처리 실패", e);
    }
  }

}

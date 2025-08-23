package com.practice.likelionhackathoncesco.infra.naverocr.service;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.S3ErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.global.config.NaverOcrConfig;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import com.practice.likelionhackathoncesco.infra.naverocr.dto.ImageDto;
import com.practice.likelionhackathoncesco.infra.naverocr.dto.request.OcrRequest;
import com.practice.likelionhackathoncesco.infra.naverocr.dto.response.OcrResponse;
import com.practice.likelionhackathoncesco.infra.naverocr.dto.response.RoadAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@Setter
public class NaverOcrService {

  private final AmazonS3 amazonS3; // AWS SDK에서 제공하는 S3 클라이언트 객체
  private final S3Config s3Config; // 버킷 이름과 경로 등 설정 정보
  private final RestTemplate restTemplate; // RestAPI 호출용
  private final AnalysisReportRepository analysisReportRepository;
  private final NaverOcrConfig naverOcrConfig; // API InvokeUrl, seceretKey

  // ocr로 텍스트 추출 -> 분석 리포트 등기부등본!!!!!!!!!!
  public OcrResponse extractText(Long reportId) {

    AnalysisReport analysisReport =
        analysisReportRepository
            .findById(reportId)
            .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    try {
      log.info("OCR 처리 시작: s3key={}", analysisReport.getS3Key());

      // s3에서 파일 다운로드
      // S3ObjectInputStream은 네트워크 연결을 유지하는 스트림이기 때문에 사용 후 닫아야 함(try-with-resource구문)
      try {

        // OCR API 요청 생성
        OcrRequest requestDto =
            createOcrRequest(analysisReport.getS3Key(), analysisReport.getFileName());

        // DB에 진행 상태 필드 업데이트
        analysisReport.updateProcessingStatus(ProcessingStatus.OCR_PROCESSING);
        analysisReportRepository.save(analysisReport);

        // Ocr API 호출
        OcrResponse ocrResult = callOcrApi(requestDto);

        log.info("OCR 처리 완료: reportId={}", reportId);

        /*// 추출된 텍스트 DB에 저장
        analysisReport.updateOcrText(ocrResult.getSections().toString()); // toString()으로 저장*/

        // DB에 진행 상태 필드 업데이트
        analysisReport.updateProcessingStatus(ProcessingStatus.OCR_COMPLETED);
        analysisReportRepository.save(analysisReport);

        return ocrResult;

      } catch (CustomException e) {
        throw new CustomException(S3ErrorCode.FILE_DOWNLOAD_FAIL);
      }

    } catch (CustomException e) { // 추후에 ocrErrorCode 작성 후 예외 던지기
      log.error("OCR 처리 중 예상치 못한 오류: s3key={}", analysisReport.getS3Key(), e);
      return OcrResponse.builder()
          .processingStatus(ProcessingStatus.FAILED) // ocr 실패
          .build();
    } catch (IOException e) { // callOcrApi()에서 IOException을 던지고 있기 때문에 받아서 다시 던져야 함
      throw new RuntimeException(e);
    }
  }

  // pdf 전용 ocr 요청 생성
  protected OcrRequest createOcrRequest(String s3key, String fileName) {

    // s3 객체 url로 요청을 보냄
    String s3Url = amazonS3.getUrl(s3Config.getBucket(), s3key).toString();

    log.info("생성된 S3 URL: {}", s3Url);
    log.info("버킷명: {}, S3 키: {}", s3Config.getBucket(), s3key);

    // 공식 문서 기준 이미지 요청 방식
    ImageDto pdfImage = ImageDto.builder().format("pdf").name(fileName).url(s3Url).build();

    // 공식 문서 기준 요청 방식
    return OcrRequest.builder()
        .version("V2")
        .requestId("pdf-" + System.currentTimeMillis()) // 임의의 API 호출 UUID
        .timestamp(System.currentTimeMillis()) // 임의의 API 호출 시각
        .lang("ko") // OCR 인식 요청 언어 정보
        .enableTableDetection(true) // 표 형태 제공 (우리는 등기부등본이기 때문에 표 형태로 제공받아야 보기 편할 듯)
        .images(List.of(pdfImage)) // JSON Array로 작성, 호출당 1개의 이미지 Array 작성 가능, 이미지 크기: 최대 50MB
        .build();
  }

  // OCR API 호출하여 파싱된 데이터 반환
  protected OcrResponse callOcrApi(OcrRequest request) throws IOException {
    try {
      // HTTP 헤더 설정 (공식 문서 -> X-OCR-SECRET / Content-Type 2가지 필드 필요
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("X-OCR-SECRET", naverOcrConfig.getSecretKey());

      // API 요청 엔티티 생성 (OcrRequest DTO와 헤더를 함께 포장해서 전송)
      HttpEntity<OcrRequest> requestEntity = new HttpEntity<>(request, headers);

      log.info("Naver OCR API 호출 시작: requestId={}", request.getRequestId());

      // 이 요청방식 대로 요청을 보내면 응답을 받을 수 있음 -> 응답을 생성
      ResponseEntity<String> response =
          restTemplate.exchange(
              naverOcrConfig.getInvokeUrl(), // api 엔드포인트
              HttpMethod.POST, // http 메서드
              requestEntity, // 헤더와 생성한 요청
              String.class // 응답 타입
          );

      // 응답에서 텍스트 파싱 (응답을 정리한다고 생각) 하여 반환
      return parseResponse(response);

    } catch (Exception e) {
      log.error("OCR API 호출 실패", e);
      throw new IOException("OCR API 호출 실패", e);
    }
  }

  protected OcrResponse parseResponse(ResponseEntity<String> response) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode root = objectMapper.readTree(response.getBody());

    JsonNode imagesNode = root.path("images"); // 이미지 배열로 저장되어 있음
    log.info("총 페이지 수: {}", imagesNode.size());

    // 모든 텍스트를 순서대로 수집
    List<String> allTexts = new ArrayList<>();

    // 모든 페이지를 순회하여 텍스트 수집
    for (int pageIndex = 0; pageIndex < imagesNode.size(); pageIndex++) {
      JsonNode currentPage = imagesNode.get(pageIndex);
      JsonNode tablesNode = currentPage.path("tables");

      log.info("페이지 {} 처리 중, 테이블 수: {}", pageIndex + 1, tablesNode.size());

      if (tablesNode.isArray()) {
        for (JsonNode table : tablesNode) {
          List<String> tableTexts = extractTextsFromTable(table);
          allTexts.addAll(tableTexts);
          log.info("테이블에서 {}개 텍스트 추출", tableTexts.size());
        }
      }
    }

    log.info("전체 텍스트 수집 완료: {}개", allTexts.size());

    // 마커 기반으로 섹션 구분 (표제부, 갑구, 을구)
    Map<String, List<String>> sections = parseByMarkers(allTexts);

    log.info("전체 처리 완료 - 총 섹션 수: {}", sections.size());

    return OcrResponse.builder()
        .sections(sections)
        .processingStatus(ProcessingStatus.OCR_COMPLETED)
        .build();
  }

  // 마커 기반 섹션 파싱
  private Map<String, List<String>> parseByMarkers(List<String> allTexts) {
    Map<String, List<String>> sections = new LinkedHashMap<>();

    String currentSection = "표제부"; // 기본적으로 표제부로 시작
    List<String> currentTexts = new ArrayList<>();

    for (int i = 0; i < allTexts.size(); i++) {
      String text = allTexts.get(i).trim();

      // 섹션 마커 감지
      String detectedMarker = detectSectionMarker(i, allTexts);

      if (detectedMarker != null) {
        // 이전 섹션 저장
        if (!currentTexts.isEmpty()) {
          List<String> cleanedTexts = cleanTexts(currentTexts);
          if (!cleanedTexts.isEmpty()) {
            sections.put(currentSection, cleanedTexts);
            log.info("섹션 완료: {}, 텍스트 수: {}", currentSection, cleanedTexts.size());
          }
        }

        // 새 섹션 시작
        currentSection = detectedMarker;
        currentTexts = new ArrayList<>();
        log.info("새 섹션 시작: {} (인덱스: {})", currentSection, i);

        // 마커 텍스트들 건너뛰기
        i = skipMarkerTexts(i, allTexts, detectedMarker);
        continue;
      }

      // 현재 섹션에 텍스트 추가
      if (!text.isEmpty()) {
        currentTexts.add(text);
      }
    }

    // 마지막 섹션 저장
    if (!currentTexts.isEmpty()) {
      List<String> cleanedTexts = cleanTexts(currentTexts);
      if (!cleanedTexts.isEmpty()) {
        sections.put(currentSection, cleanedTexts);
        log.info("마지막 섹션 완료: {}, 텍스트 수: {}", currentSection, cleanedTexts.size());
      }
    }

    return sections;
  }

  // 섹션 마커 감지 (【 표 제 부 】, 【 갑 구 】, 【 을 구 】)
  private String detectSectionMarker(int startIndex, List<String> texts) {
    // 최소 검사 범위 확보
    if (startIndex >= texts.size()) {
      return null;
    }

    // "( 소유권에" + "관한 사항 )" 패턴으로 갑구 확실히 감지 (최우선)
    if (isOwnershipRightsPattern(startIndex, texts)) {
      log.info("갑구 마커 발견 - '소유권에 관한 사항' 패턴 (인덱스: {})", startIndex);
      return "갑구";
    }

    // "을" + "구" 패턴으로 을구 감지 (갑구보다 우선)
    if (isEulguMarkerPattern(startIndex, texts)) {
      log.info("을구 마커 발견 (인덱스: {})", startIndex);
      return "을구";
    }

    // 갑구 마커 감지 (표제부보다 먼저)
    if (isGapguMarkerPattern(startIndex, texts)) {
      log.info("갑구 마커 발견 (인덱스: {})", startIndex);
      return "갑구";
    }

    // 표제부 마커 감지 (가장 나중에 확인)
    String currentText = texts.get(startIndex).trim();
    if (currentText.equals("표제부")) {
      log.info("표제부 마커 발견 (인덱스: {})", startIndex);
      return "표제부";
    }

    return null;
  }

  // "( 소유권에" + "관한 사항 )" 패턴 감지 (갑구의 확실한 신호)
  private boolean isOwnershipRightsPattern(int startIndex, List<String> texts) {
    // 현재 위치부터 앞뒤로 검색
    int searchStart = Math.max(0, startIndex - 2);
    int searchEnd = Math.min(texts.size() - 1, startIndex + 10);

    log.debug("소유권에 관한 사항 패턴 검사 범위: {} ~ {}", searchStart, searchEnd);

    boolean foundOwnership = false;
    boolean foundRights = false;
    boolean foundMatter = false;

    // "소유권에", "관한", "사항" 패턴 찾기
    for (int i = searchStart; i <= searchEnd; i++) {
      String token = texts.get(i).trim();

      if (token.contains("소유권에") || token.equals("( 소유권에") || token.equals("소유권에")) {
        foundOwnership = true;
        log.debug("'소유권에' 토큰 발견: 인덱스 {}, 텍스트: '{}'", i, token);
      }

      if (token.contains("관한") || token.equals("관한")) {
        foundRights = true;
        log.debug("'관한' 토큰 발견: 인덱스 {}, 텍스트: '{}'", i, token);
      }

      if (token.contains("사항") || token.equals("사항") || token.equals("사항 )") || token.contains("사항 )")) {
        foundMatter = true;
        log.debug("'사항' 토큰 발견: 인덱스 {}, 텍스트: '{}'", i, token);
      }
    }

    // 세 키워드가 모두 발견되고 "이외의"가 없으면 갑구
    if (foundOwnership && foundRights && foundMatter) {
      // "이외의" 키워드가 주변에 있는지 확인 (을구와 구분)
      boolean hasEoiOe = false;
      for (int i = searchStart; i <= searchEnd; i++) {
        String token = texts.get(i).trim();
        if (token.contains("이외의") || token.contains("이외")) {
          hasEoiOe = true;
          log.debug("'이외의' 키워드 발견 - 을구로 판단");
          break;
        }
      }

      if (!hasEoiOe) {
        log.debug("갑구 확정: '소유권에 관한 사항' 패턴 (이외의 키워드 없음)");
        return true;
      }
    }

    return false;
  }

  // 을구 마커 패턴 확인
  private boolean isEulguMarkerPattern(int startIndex, List<String> texts) {
    if (startIndex + 4 >= texts.size()) {
      return false;
    }

    String token1 = texts.get(startIndex).trim();
    String token2 = texts.get(startIndex + 1).trim();

    // "을" + "구" 패턴 확인
    if (token1.equals("을") && token2.equals("구")) {
      // 다음 몇 개 토큰에서 "소유권 이외의" 확인 (실제 을구 섹션인지 검증)
      for (int i = startIndex + 2; i < Math.min(startIndex + 10, texts.size()); i++) {
        String checkText = texts.get(i).trim();
        if (checkText.contains("소유권") && checkText.contains("이외")) {
          log.debug("을구 패턴 확인: '{}' + '{}' + '소유권 이외의' 발견", token1, token2);
          return true;
        }
        if (checkText.contains("권리에") && checkText.contains("관한")) {
          log.debug("을구 패턴 확인: '{}' + '{}' + '권리에 관한' 발견", token1, token2);
          return true;
        }
      }
    }

    // "을구" 단일 토큰 + "소유권 이외의" 확인
    if (token1.equals("을구")) {
      for (int i = startIndex + 1; i < Math.min(startIndex + 8, texts.size()); i++) {
        String checkText = texts.get(i).trim();
        if (checkText.contains("소유권") && checkText.contains("이외")) {
          log.debug("을구 단일 토큰 + 소유권 이외의 확인");
          return true;
        }
      }
    }

    return false;
  }

  // 갑구 마커 패턴 확인 - 기존 갑구 패턴들
  private boolean isGapguMarkerPattern(int startIndex, List<String> texts) {
    // 기본 범위 체크
    if (startIndex + 1 >= texts.size()) {
      return false;
    }

    String token1 = texts.get(startIndex).trim();
    String token2 = texts.get(startIndex + 1).trim();

    log.debug("갑구 패턴 검사: '{}' + '{}'", token1, token2);

    // "갑" + "구" 직접 패턴
    if (token1.equals("갑") && token2.equals("구")) {
      log.debug("갑구 직접 패턴 발견: '{}' + '{}'", token1, token2);
      return true;
    }

    // "갑구" 단일 토큰
    if (token1.equals("갑구")) {
      log.debug("갑구 단일 토큰 발견: '{}'", token1);
      return true;
    }

    // "| 갑" + "구" 패턴
    if ((token1.equals("|") || token1.contains("갑")) && token2.equals("구")) {
      log.debug("갑구 특수 패턴: '{}' + '{}'", token1, token2);
      return true;
    }

    return false;
  }

  // 마커 텍스트들 건너뛰기
  private int skipMarkerTexts(int startIndex, List<String> texts, String sectionType) {
    int skipCount = 1; // 기본적으로 1개 건너뛰기

    if ("갑구".equals(sectionType)) {
      // "| 갑" + "구" 패턴인 경우 2개 건너뛰기
      if (startIndex + 1 < texts.size() && texts.get(startIndex).contains("|")
          || texts.get(startIndex).equals("갑")) {
        skipCount = 2;
      }
    } else if ("을구".equals(sectionType)) {
      // "을" + "구" 패턴인 경우 2개 건너뛰기
      if (startIndex + 1 < texts.size()
          && texts.get(startIndex).equals("을")
          && texts.get(startIndex + 1).equals("구")) {
        skipCount = 2;
      }
    }

    return Math.min(startIndex + skipCount, texts.size() - 1);
  }

  // 텍스트 정리
  private List<String> cleanTexts(List<String> texts) {
    return texts.stream()
        .filter(text -> text != null && !text.trim().isEmpty())
        .filter(text -> !isNoiseText(text))
        .map(String::trim)
        .collect(Collectors.toList());
  }

  // 테이블에서 텍스트 추출
  private List<String> extractTextsFromTable(JsonNode table) {
    List<String> inferTexts = new ArrayList<>();
    JsonNode cells = table.path("cells");

    if (cells.isArray()) {
      for (JsonNode cell : cells) {
        JsonNode cellTextLines = cell.path("cellTextLines");
        if (cellTextLines.isArray()) {
          for (JsonNode line : cellTextLines) {
            JsonNode cellWords = line.path("cellWords");
            if (cellWords.isArray()) {
              for (JsonNode word : cellWords) {
                String text = word.path("inferText").asText().trim();
                if (!text.isEmpty()) {
                  inferTexts.add(text);
                }
              }
            }
          }
        }
      }
    }
    return inferTexts;
  }

  // 노이즈 텍스트 필터링
  private boolean isNoiseText(String text) {
    String trimmed = text.trim();
    return trimmed.equals("(")
        || trimmed.equals(")")
        || trimmed.equals("】")
        || trimmed.equals("【")
        || trimmed.equals("[")
        || trimmed.equals("]")
        || trimmed.equals("|")
        || trimmed.equals("■")
        || trimmed.equals("▣")
        || (trimmed.length() == 1
        && !Character.isDigit(trimmed.charAt(0))
        && !trimmed.matches("[가-힣a-zA-Z]")); // 한글, 영문, 숫자가 아닌 한 글자
  }
}
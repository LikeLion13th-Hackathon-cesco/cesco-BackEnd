package com.practice.likelionhackathoncesco.naverocr.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.S3ErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.global.config.NaverOcrConfig;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import com.practice.likelionhackathoncesco.naverocr.dto.ImageDto;
import com.practice.likelionhackathoncesco.naverocr.dto.request.OcrRequest;
import com.practice.likelionhackathoncesco.naverocr.dto.response.OcrResponse;
import com.practice.likelionhackathoncesco.naverocr.dto.response.RoadAddress;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    try {
      log.info("OCR 처리 시작: s3key={}", analysisReport.getS3Key());

      // s3에서 파일 다운로드
      // S3ObjectInputStream은 네트워크 연결을 유지하는 스트림이기 때문에 사용 후 닫아야 함(try-with-resource구문)
      try {

        // OCR API 요청 생성
        OcrRequest requestDto = createOcrRequest(analysisReport.getS3Key(), analysisReport.getFileName());

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

  /*// S3에서 사용자가 업로드한 등기부등본 pdf파일 다운로드
  // 인코딩 진행 X -> image를 url로 ocr api한테 보내면 인코딩 필요 없음 (s3 객체 url로 전송 -> 누구나 접근 가능함)
  protected InputStream downloadPdfFromS3(Long reportId) {
    log.info("S3에서 PDF 다운로드 시작: reportId={}", reportId);

    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    String s3Key = analysisReport.getS3Key();

    try {
      if (!amazonS3.doesObjectExist(s3Config.getBucket(), s3Key)) {
        throw new CustomException(S3ErrorCode.FILE_NOT_FOUND);
      }

      S3Object s3Object = amazonS3.getObject(s3Config.getBucket(), s3Key); // 파일을 가져올 때 getObject()를 사용함

      return s3Object.getObjectContent();

    } catch (CustomException e) {
      log.error("S3 파일 다운로드 실패: {}", s3Key, e);
      throw new CustomException(S3ErrorCode.FILE_DOWNLOAD_FAIL);
    }
  }*/

  // pdf 전용 ocr 요청 생성
  protected OcrRequest createOcrRequest(String s3key, String fileName) {

    // s3 객체 url로 요청을 보냄
    String s3Url = amazonS3.getUrl(s3Config.getBucket(), s3key).toString();

    log.info("생성된 S3 URL: {}", s3Url);
    log.info("버킷명: {}, S3 키: {}", s3Config.getBucket(), s3key);

    // 공식 문서 기준 이미지 요청 방식
    ImageDto pdfImage = ImageDto.builder()
        .format("pdf")
        .name(fileName)
        .url(s3Url)
        .build();

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
      ResponseEntity<String> response = restTemplate.exchange(
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
    JsonNode root = objectMapper.readTree(response.getBody()); // 응답 바디를 트리 구조로 파싱해서 jsonNode 객체로 생성

    Map<String, List<String>> result = new LinkedHashMap<>(); // 표제부, 갑구, 을구 순서 보장
    List<String> fallbackNames = List.of("표제부", "갑구", "을구");
    RoadAddress roadAddress = null; // 도로명주소 객체

    // 응답 바디의 image 배열의 첫번째 요소(ocr한 첫 페이지)의 table(표)를 가져옴
    JsonNode tablesNode = root
        .path("images")
        .get(0)
        .path("tables");

    int index = 0;

    if (tablesNode.isArray()) {
      for (JsonNode table : tablesNode) {
        // 테이블 제목 추출: "표제부", "갑구", "을구"
        String sectionName = table.path("title").path("inferText").asText().trim();

        if (sectionName.isEmpty()) {
          sectionName = fallbackNames.get(index);
        }

        index++;

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
                    String text = word.path("inferText").asText();
                    inferTexts.add(text);
                  }
                }
              }
            }
          }
        }

        result.put(sectionName, inferTexts);

        /*if ("표제부".equals(sectionName)) {
          roadAddress = extractRoadAddress(inferTexts);
        }*/
      }
    }
    return OcrResponse.builder()
        .sections(result)
        //.roadAddress(roadAddress)
        .processingStatus(ProcessingStatus.OCR_COMPLETED)
        .build();

  }

  // 도로명 주소만 파싱 (codef api 호출용) -> codef api 사용 안하기로 결정
  private RoadAddress extractRoadAddress(List<String> inferTexts) {
    log.info("도로명주소 추출 시작, 총 텍스트 개수: {}", inferTexts.size());

    int roadAddressIndex = -1;
    for (int i = 0; i < inferTexts.size(); i++) {
      String text = inferTexts.get(i).trim();
      log.info("검사중인 텍스트: '{}'", text);

      // 여러 패턴으로 매칭 시도
      if (text.contains("도로명주소") ||
          text.contains("[도로명주소]") ||
          text.equals("[도로명주소]")) {
        roadAddressIndex = i;
        log.info("도로명주소 키워드 발견! 인덱스: {}, 텍스트: '{}'", i, text);
        break;
      }
    }

    if (roadAddressIndex == -1) {
      log.warn("도로명주소 키워드를 찾을 수 없습니다");
      return null; // 도로명주소를 찾을 수 없음
    }

    try {
      // 인덱스 범위 체크
      if (roadAddressIndex + 4 >= inferTexts.size()) {
        log.warn("도로명주소 다음 요소들이 부족합니다. 필요: {}, 실제: {}",
            roadAddressIndex + 4, inferTexts.size() - 1);
        return null;
      }

      // [도로명주소] 다음 4개 요소가 시도, 시군구, 도로명, 건물번호
      String sido = inferTexts.get(roadAddressIndex + 1).trim();
      String sigungu = inferTexts.get(roadAddressIndex + 2).trim();
      String roadName = inferTexts.get(roadAddressIndex + 3).trim();
      String buildingNumber = inferTexts.get(roadAddressIndex + 4).trim();

      log.info("추출된 도로명주소 정보 - 시도: '{}', 시군구: '{}', 도로명: '{}', 건물번호: '{}'",
          sido, sigungu, roadName, buildingNumber);

      // 빈 값 체크
      if (sido.isEmpty() || sigungu.isEmpty() || roadName.isEmpty() || buildingNumber.isEmpty()) {
        log.warn("도로명주소 정보 중 빈 값이 있습니다");
        return null;
      }

      RoadAddress result = RoadAddress.builder()
          .sido(sido)
          .sigungu(sigungu)
          .roadName(roadName)
          .buildingNumber(buildingNumber)
          .build();

      log.info("도로명주소 추출 성공: {}", result);
      return result;

    } catch (IndexOutOfBoundsException e) {
      // 도로명주소 정보가 불완전한 경우
      return null;
    }
  }

}

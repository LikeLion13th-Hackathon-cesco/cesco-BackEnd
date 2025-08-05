package com.practice.likelionhackathoncesco.naverocr.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.S3ErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import com.practice.likelionhackathoncesco.naverocr.dto.ImageDto;
import com.practice.likelionhackathoncesco.naverocr.dto.request.OcrRequest;
import com.practice.likelionhackathoncesco.naverocr.dto.response.OcrResponse;
import com.practice.likelionhackathoncesco.naverocr.global.config.NaverOcrConfig;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
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

  // ocr로 텍스트 추출
  private OcrResponse extractText(Long reportId) {

    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    try {
      log.info("OCR 처리 시작: s3key={}", analysisReport.getS3Key());

      // s3에서 파일 다운로드
      InputStream pdfFile = downloadPdfFromS3(reportId);

      // OCR API 요청 생성
      OcrRequest requestDto = createOcrRequest(analysisReport.getS3Key(),
          analysisReport.getFileName());

      // Ocr API 호출
      String ocrResult = callOcrApi(requestDto);

      log.info("OCR 처리 완료: reportId={}, 텍스트 길이={}", reportId, ocrResult.length());

      return OcrResponse.builder()
          .s3Key(analysisReport.getS3Key())
          .ocrText(ocrResult)
          // .detectedKeywords(keywords) // 감지된 언어 추후에 적용
          .processingStatus(ProcessingStatus.OCR_COMPLETED)
          .build();

    } catch (CustomException e) { // 추후에 ocrErrorCode 작성 후 예외 던지기
      log.error("OCR 처리 중 예상치 못한 오류: s3key={}", analysisReport.getS3Key(), e);
      return OcrResponse.builder()
          .s3Key(analysisReport.getS3Key()) // 파일의 s3key 반환
          .processingStatus(ProcessingStatus.FAILED) // ocr 실패
          .build();
    } catch (IOException e) { // callOcrApi()에서 IOException을 던지고 있기 때문에 받아서 다시 던져야 함
      throw new RuntimeException(e);
    }
  }

  // S3에서 사용자가 업로드한 등기부등본 pdf파일 다운로드 후 인코딩
  // InputStream을 Base64로 인코딩 (ocr API는 파일을 Base64 인코딩 된 문자열로 받기 때문)
  private InputStream downloadPdfFromS3(Long reportId) {
    log.info("S3에서 PDF 다운로드 시작: reportId={}", reportId);

    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    String s3Key = analysisReport.getS3Key();

    try {
      if (!amazonS3.doesObjectExist(s3Config.getBucket(), s3Key)) {
        throw new IllegalArgumentException("S3에 파일이 존재하지 않습니다: " + s3Key);
      }

      S3Object s3Object = amazonS3.getObject(s3Config.getBucket(), s3Key); // 파일을 가져올 때 getObject()를 사용함

      try (InputStream inputStream = s3Object.getObjectContent()) { // 다운로드 된 파일 인코딩 실행
        byte[] fileBytes = inputStream.readAllBytes();
      } catch (Exception e) {
        log.error("S3 파일 다운로드/인코딩 실패: {}", s3Key, e);
        throw new IOException("파일 처리 실패: " + s3Key, e);
      }

      return s3Object.getObjectContent();

    } catch (Exception e) {
      log.error("S3 파일 다운로드 실패: {}", s3Key, e);
      throw new RuntimeException("S3 파일 다운로드 실패: " + s3Key, e);
    }
  }

  // pdf 전용 ocr 요청 생성
  private OcrRequest createOcrRequest(String s3key, String fileName) {

    // s3 객체 url로 요청을 보냄
    String s3Url = amazonS3.getUrl(s3Config.getBucket(), s3key).toString();

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
        .images(List.of(pdfImage)) // JSON Array로 작성, 호출당 1개의 이미지 Array 작성 가능, 이미지 크기: 최대 50 MB
        .build();
  }

  // OCR API 호출
  private String callOcrApi(OcrRequest request) throws IOException {
    try {
      // HTTP 헤더 설정 (공식 문서 -> X-OCR-SECRET / Content-Type 2가지 필드 필요
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("X-OCR-SECRET", naverOcrConfig.getSecretKey());

      // API 요청 실행 (OcrRequest DTO를 직접 전송)
      HttpEntity<OcrRequest> requestEntity = new HttpEntity<>(request, headers);

      log.info("Naver OCR API 호출 시작: requestId={}", request.getRequestId());

      ResponseEntity<String> response = restTemplate.exchange(
          naverOcrConfig.getInvokeUrl(),
          HttpMethod.POST,
          requestEntity,
          String.class
      );

      // 응답에서 텍스트 파싱 (응답을 정리한다고 생각) -> 추후 확장
      // return extractTextFromResponse(response.getBody());
      return response.getBody();

    } catch (Exception e) {
      log.error("OCR API 호출 실패", e);
      throw new IOException("OCR API 호출 실패", e);
    }
  }

  /*// Ocr API 응답 파싱
  private String extractTextFromResponse(String responseBody) throws IOException {
    try {
      // API 응답을 JSON으로 파싱함
      JsonNode response = objectMapper.readTree(responseBody);
      JsonNode images = response.get("images");

      // images 배열 존재 확인 (인코딩된 pdf 문서가 담긴 배열)
      if (images == null || !images.isArray() || images.size() == 0) {
        log.warn("OCR 응답에서 images 배열을 찾을 수 없음");
        return "";
      }

      // 배열의 첫 번째 이미지 결과 가져오기 (PDF는 단일 처리)
      JsonNode imageResult = images.get(0);

    }
  }*/


}

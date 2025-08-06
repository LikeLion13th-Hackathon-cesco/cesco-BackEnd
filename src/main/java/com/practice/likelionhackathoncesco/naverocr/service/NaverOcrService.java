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
import com.practice.likelionhackathoncesco.naverocr.exception.OcrErrorCode;
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
  public OcrResponse extractText(Long reportId) {

    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    try {
      log.info("OCR 처리 시작: s3key={}", analysisReport.getS3Key());

      // s3에서 파일 다운로드
      downloadPdfFromS3(reportId);

      // OCR API 요청 생성
      OcrRequest requestDto = createOcrRequest(analysisReport.getS3Key(), analysisReport.getFileName());

      // DB에 진행 상태 필드 업데이트
      analysisReport.updateProcessingStatus(ProcessingStatus.OCR_PROCESSING);
      analysisReportRepository.save(analysisReport);

      // Ocr API 호출
      String ocrResult = callOcrApi(requestDto);

      log.info("OCR 처리 완료: reportId={}, 텍스트 길이={}", reportId, ocrResult.length());

      // DB에 진행 상태 필드 업데이트
      analysisReport.updateProcessingStatus(ProcessingStatus.OCR_COMPLETED);
      analysisReportRepository.save(analysisReport);

      return OcrResponse.builder()
          .s3Key(analysisReport.getS3Key())
          .ocrText(ocrResult)
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

  // S3에서 사용자가 업로드한 등기부등본 pdf파일 다운로드
  // 인코딩 진행 X -> image를 url로 ocr api한테 보내면 인코딩 필요 없음 (s3 객체 url로 전송 -> 누구나 접근 가능함)
  private InputStream downloadPdfFromS3(Long reportId) {
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
  }

  // pdf 전용 ocr 요청 생성
  private OcrRequest createOcrRequest(String s3key, String fileName) {

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

      // 이 요청방식 대로 요청을 보내면 응답을 받을 수 있음
      ResponseEntity<String> response = restTemplate.exchange(
          naverOcrConfig.getInvokeUrl(),
          HttpMethod.POST,
          requestEntity, // 헤더와 생성한 요청
          String.class
      );

      // 응답에서 텍스트 파싱 (응답을 정리한다고 생각) -> 추후 확장 !!! 응답 정리할 필요 없음
      return response.getBody(); // 응답의 바디만 리턴 (공식문서 기준 응답 형태로 제공됨)

    } catch (Exception e) {
      log.error("OCR API 호출 실패", e);
      throw new IOException("OCR API 호출 실패", e);
    }
  }

}

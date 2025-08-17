package com.practice.likelionhackathoncesco.naverocr.service;

import com.practice.likelionhackathoncesco.domain.analysisreport.exception.S3ErrorCode;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.FraudRegisterReport;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.ReportStatus;
import com.practice.likelionhackathoncesco.domain.fraudreport.repository.FraudRegisterReportRepository;
import com.practice.likelionhackathoncesco.global.config.NaverOcrConfig;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import com.practice.likelionhackathoncesco.naverocr.dto.request.OcrRequest;
import com.practice.likelionhackathoncesco.naverocr.dto.response.OcrResponse;
import java.io.IOException;
import java.util.ArrayList;
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
public class FraudOcrService {

  private final RestTemplate restTemplate; // RestAPI 호출용
  private final FraudRegisterReportRepository fraudRegisterReportRepository; // 신고할 등기부등본
  private final NaverOcrConfig naverOcrConfig; // API InvokeUrl, seceretKey

  private final NaverOcrService naverOcrService;

  // ocr로 텍스트 추출 -> 신고할 등기부등본!!!!!!!!!! (텍스트로 반환)
  public List<String> gapguExtractText(Long reportId) {

    FraudRegisterReport fraudRegisterReport =
        fraudRegisterReportRepository
            .findById(reportId)
            .orElseThrow(() -> new CustomException(S3ErrorCode.FILE_NOT_FOUND));

    try {

      log.info("OCR 처리 시작: s3key={}", fraudRegisterReport.getS3Key());

      // OCR API 요청 생성
      OcrRequest requestDto =
          naverOcrService.createOcrRequest(
              fraudRegisterReport.getS3Key(), fraudRegisterReport.getFileName());

      // DB에 진행 상태 필드 업데이트
      fraudRegisterReport.updateReportStatus(ReportStatus.OCR_PROCESSING);
      fraudRegisterReportRepository.save(fraudRegisterReport);

      // Ocr API 호출
      List<String> resultGapgu = callOcrApi(requestDto);

      log.info("OCR 처리 완료: reportId={}", reportId);

      // DB에 진행 상태 필드 업데이트
      fraudRegisterReport.updateReportStatus(ReportStatus.OCR_COMPLETED);
      fraudRegisterReportRepository.save(fraudRegisterReport);

      return resultGapgu;

    } catch (CustomException e) {
      throw new CustomException(S3ErrorCode.FILE_DOWNLOAD_FAIL);
    } catch (IOException e) { // callOcrApi()에서 IOException을 던지고 있기 때문에 받아서 다시 던져야 함
      throw new RuntimeException(e);
    }
  }

  // OCR API 호출하여 파싱된 데이터 반환
  private List<String> callOcrApi(OcrRequest request) throws IOException {
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
      return parseGapgu(response);

    } catch (Exception e) {
      log.error("OCR API 호출 실패", e);
      throw new IOException("OCR API 호출 실패", e);
    }
  }

  private List<String> parseGapgu(ResponseEntity<String> response) throws IOException {
    // 기존 parseResponse 로직을 사용해서 전체 파싱
    OcrResponse ocrResponse = naverOcrService.parseResponse(response); // 기존 메소드 재사용

    // 갑구만 반환
    return ocrResponse.getSections().getOrDefault("갑구", new ArrayList<>());
  }
}

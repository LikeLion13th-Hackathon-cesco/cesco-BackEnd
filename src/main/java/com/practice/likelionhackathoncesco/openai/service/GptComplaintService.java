package com.practice.likelionhackathoncesco.openai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.likelionhackathoncesco.domain.fraudreport.dto.response.FakerResponse;
import com.practice.likelionhackathoncesco.domain.fraudreport.entity.Faker;
import com.practice.likelionhackathoncesco.domain.fraudreport.repository.FakerRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import com.practice.likelionhackathoncesco.naverocr.service.FraudOcrService;
import com.practice.likelionhackathoncesco.openai.dto.response.GptComplaintResponse;
import com.practice.likelionhackathoncesco.openai.dto.response.GptOwnerListResponse;
import com.practice.likelionhackathoncesco.openai.exception.GptErrorCode;
import com.practice.likelionhackathoncesco.openai.global.config.GptConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptComplaintService {

  // gpt API 에 신고용 등기부등본 추출 택스트 전달해서 신고당한 임대인 정보 추출

  private final GptConfig gptConfig;
  private final FraudOcrService fraudOcrService;
  private final ObjectMapper objectMapper;
  private final FakerRepository fakerRepository;

  // 신고 당한 임대인 정보 DB에 저장하는 메소드
  @Transactional
  public List<FakerResponse> saveFakerInfo(List<GptComplaintResponse> responseList) {
    // 리스트로 반환되는 임대인 정보를 리스트 인덱스 단위로 객체 생성
    List<Faker>fakerList = responseList.stream()
        .map(r->Faker.builder()
            .fakerName(r.getFakerName())
            .residentNum(r.getResidentNum())
            .build()).toList();
    // DB 저장
    fakerRepository.saveAll(fakerList);

    // 저장된 엔티티 DTO로 변환 후 반환
    return fakerList.stream().map(f->new FakerResponse(f.getFakerName(), f.getResidentNum())).toList();
        
  }


  // 신고용 등기부등본에서 임대인 정보 요청을 위해 gpt-4o용 프롬프트 생성
  public List<Map<String, String>> createGetFakerPrompt(Long complaintReportId) throws JsonProcessingException {

    // 신고 등기부등본 갑구 파싱 택스트 바로 가져오기
    List<String> text = fraudOcrService.gapguExtractText(complaintReportId);
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonText = objectMapper.writeValueAsString(text);

    List<Map<String, String>> prompts = new ArrayList<>();

    // gpt에게 행동지침을 주는 역할의 프롬프트
    prompts.add(Map.of("role", "system", "content", "너는 한국 등기부등본 문서를 분석해서 소유자의 정보를 추출하는 전문 AI 어시스턴트야."));

    // gpt에게 사용자가 질문하거나 지시하는 메시지 -> JSON 형식으로 응답해달라는 것 반드시 명시
    prompts.add(Map.of("role", "user", "content", String.format("""
        아래는 ocr로 추출한 등기부등본 갑구에서 추출한 택스트야.
        여기서 소유자의 이름과 주민등록번호 앞6자리(생년월일 부분)만 추출해서 JSON 형식으로 반환해줘.
        소유자가 한명 이상일때는 배열로 모두 알려줘.
        
        OCR 결과:
        %s
        
        반환 형식 예시:
        {
          "faker" : [
            { "name" : "홍길동", "residentNum" : "660101" },
            { "name" : "신짱구", "residentNum" : "701106" }
          ]
        }
        """, String.join("\n", jsonText))));

    return prompts;
  }


  // gpt-4o API 불러오는 메소드
  public String callGptAPI(List<Map<String, String>> prompts, String complaintReportId) {

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();

    // requestBody
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", gptConfig.getModel());
    requestBody.put("messages", prompts);
    requestBody.put("temperature", 0.7);  // gpt의 답변 창의성 정도 -> 0.7이 중간정도


    HttpHeaders headers = new HttpHeaders();  // 요청에 붙은 http 헤더로 API Key, 데이터타입 포함
    headers.setContentType(MediaType.APPLICATION_JSON);   // http 헤더에 요청이 JSON 형식이라고 지정 추가
    headers.setBearerAuth(gptConfig.getSecretKey());    // http 헤더에 api key 추가

    // 요청 객체 생성 (헤더와 바디 포함)
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    try{
      log.info("[GptComplaintService] GPT API 요청 시도 : complaintReportId={}",complaintReportId);
      // POST 요청
      ResponseEntity<Map> response = restTemplate.postForEntity(gptConfig.getUrl(), requestEntity, Map.class);

      if(response.getStatusCode() != HttpStatus.OK){  // 응답코드 200일때만 응답 꺼내기
        log.error("[GptComplaintService] GPT API 응답 실패 : complaintReportId={}, httpStatus={}", complaintReportId, response.getStatusCode());
        throw new CustomException(GptErrorCode.GPT_API_CALL_FAILED);
      }

      Map<String,Object> responseBody = response.getBody();   // requestBody 꺼내기
      // gpt 응답은 항상 choices 라는 배열 갖고 있음 -> choices 변수에 따로 저장 필요

      // 이부분 경고가 계속 띀!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      List<Map<String, Object>> choices;
      choices = objectMapper.convertValue(responseBody.get("choices"),
          new TypeReference<>() {}
      );

      if(choices == null || choices.isEmpty()){
        log.warn("[GptComplaintService] GPT API 응답 성공했으나 choices 변수 null : complaintReportId={}",complaintReportId);
        throw new CustomException(GptErrorCode.GPT_EMPTY_RESPONSE);
      }

      // message 변수에 gpt 응답이 담김
      Map<String,Object> message = objectMapper.convertValue(choices.get(0).get("message"), new TypeReference<Map<String,Object>>(){});
      String content = ((String) message.get("content")).replaceAll("```json", "").replaceAll("```", "").trim(); // message안에는 role과 content가 있는데 이중 content가 진짜 답변!

      log.info("[GptComplaintService] 응답 성공 : complaintReportId={}, content={}", complaintReportId, content);
      return content; // 이게 찐 gpt 응답 텍스트!

    }catch (HttpClientErrorException e) {
      log.error("[GptComplaintService] GPT API 클라이언트 오류: complaintReportId={}, {}", complaintReportId, e.getResponseBodyAsString());
      throw new CustomException(GptErrorCode.GPT_INVALID_PROMPT);

    } catch (ResourceAccessException e) {
      log.error("[GptComplaintService] GPT API 타임아웃 또는 접근 실패: complaintReportId={}, {}", complaintReportId, e.getMessage());
      throw new CustomException(GptErrorCode.GPT_TIMEOUT);

    } catch (Exception e) {
      log.error("[GptComplaintService] GPT API 호출 중 예외 발생: complaintReportId={}, {}", complaintReportId, e.getMessage());
      throw new CustomException(GptErrorCode.GPT_API_CALL_FAILED);
    }
  }

  // gpt-4o API 응답 파싱 메소드
  public GptOwnerListResponse parseGptOwnerListResponse(String content) {
    try{
      return objectMapper.readValue(content, GptOwnerListResponse.class);
    }catch (JsonProcessingException e){
      throw new CustomException(GptErrorCode.GPT_RESPONSE_PARSING_FAILED);
    }
  }

  // 배열 형태의 gpt-4o 응답 리스트로 반환
  public List<GptComplaintResponse> parseGptComplaintResponseList(String content) {
    GptOwnerListResponse gptOwnerListResponse = parseGptOwnerListResponse(content);
    return gptOwnerListResponse.getFaker().stream().map(f->new GptComplaintResponse(f.getFakerName(), f.getResidentNum())).toList();
  }

}

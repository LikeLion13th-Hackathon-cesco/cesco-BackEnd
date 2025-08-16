package com.practice.likelionhackathoncesco.openai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.AnalysisReportErrorCode;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import com.practice.likelionhackathoncesco.naverocr.dto.response.OcrResponse;
import com.practice.likelionhackathoncesco.naverocr.service.NaverOcrService;
import com.practice.likelionhackathoncesco.openai.dto.request.GptAnalysisRequest;
import com.practice.likelionhackathoncesco.openai.dto.response.GptResponse;
import com.practice.likelionhackathoncesco.openai.exception.GptErrorCode;
import com.practice.likelionhackathoncesco.openai.global.config.GptConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

  private final GptConfig gptConfig;
  private final ObjectMapper objectMapper;
  private final NaverOcrService naverOcrService;
  private final AnalysisReportRepository analysisReportRepository;


  // 보증보험 심사에서 긍정적/부정적으로 심사될 수 있다는 느낌으로 + 주소 반환하게

  // 프롬프트 생성 메소드
  public List<Map<String, String>> createPrompt(GptAnalysisRequest gptAnalysisRequest, Long reportId) throws JsonProcessingException {

    Integer officalPrice = 0;  // 이거 나중에 공시가격 api로 가져와야함!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    // ocr로 추출한 택스트 바로 가져오기
    OcrResponse ocrResponse = naverOcrService.extractText(reportId);
    Map<String, List<String>> text = ocrResponse.getSections();
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonText = objectMapper.writeValueAsString(text);

    List<Map<String, String>> prompts = new ArrayList<>();

    // 전월세 여부 문자열로 변환
    String rentType = gptAnalysisRequest.getIsMonthlyRent() == 1 ? "월세" : "전세" ;

    // 분석 상태 수정 -> GPT 설명 생성 중
    AnalysisReport analysisReport = analysisReportRepository.findById(reportId)
        .orElseThrow(() -> new CustomException(AnalysisReportErrorCode.REPORT_NOT_FOUND));
    analysisReport.updateProcessingStatus(ProcessingStatus.GPT_PROCESSING);
    
    // gpt에게 행동지침을 주는 역할의 프롬프트
    prompts.add(Map.of("role", "system", "content", "너는 부동산 등기부 등본을 분석해서 위험요소를 판단하는 전문가야."));
    
    // gpt에게 사용자가 질문하거나 지시하는 메시지 -> JSON 형식으로 응답해달라는 것 반드시 명시
    prompts.add(Map.of("role", "user", "content", String.format("""
        이 부동산은 %s 계약을 하려는 중이야. 이 부동산의 보증금은 %d 원이고, 공시가격은 %d 원이야.
        아래는 ocr로 추출한 부동산 등기부 등본 택스트야.
        
        이 택스트를 보고 다음조건을 만족하면 숫자 1만 반환해줘:
        - 갑구에서 가처분, 가등기, 가압류, 압류 이 네가지 요소가 전혀 없거나 모두 말소되었고
        - 공시지가가 근저당보다 크거나 같다면
        
        위 조건을 만족하지 않으면 다음과 같은 점수를 매겨서 총합을 계산한 뒤, 그 **합계 값 하나만 정수로 반환**해줘:
        - 공시지가가 근저당보다 작으면 0
        - 말소되지 않은 가처분이 있으면 0
        - 말소되지 않은 가등기가 있으면 0
        - 말소되지 않은 가압류가 있으면 -1
        - 말소되지 않은 압류가 있으면 -2
        
        말소된 항목은 전혀 포함하지 말고, 최종 결과는 꼭 숫자 하나만 출력해줘. 예: 1, 0, -1, -2, -3
        
        위 내용처럼 숫자 반환한 다음에는 이제 등기부등본 택스트를 분석해서 아래 내용을 정확히 순서대로 작성해줘:
        1. **현재 거래가 안전한지 여부를 두 줄로 요약해서 말해줘. 단, 위에서 계산한 점수 총합이 양수가 아니면 위험하니까 계약을 하지 않도록 권유해야해** - 예시: "이 부동산은 안전하게 거래할 수 있습니다." 또는 "이 부동산은 거래에 주의가 필요합니다."
        2. **그 판단의 이유를 다음을 기반으로 10줄 정도로 설명해줘:**
          - 갑구에 존재하는 가처분, 가등기, 가압류, 압류의 유무 및 말소 여부
          - 공시지가와 근저당의 차이 그리고 보증금과의 비교
        3. **말소되지 않은 근저당 금액의 총합**
        """, rentType, gptAnalysisRequest.getDeposit(), officalPrice))); // 위치 어디로 바꾸지?

    if(gptAnalysisRequest.getIsMonthlyRent() == 0) {  // 전세 계약의 경우

      prompts.add(Map.of("role", "user", "content", String.format("""
        이 부동산의 시세는 %d야.
        다음 조건들을 보고 보증보험 가입 가능 여부를 판단해줘:
        - 위에서 매긴 점수총합이 양수가 아니라면 보증보험 가입이 불가하고
        - 보증금이 7억 이하여야만 보증보험 가입이 가능하고
        - 말소되지 않은 근저당의 총합이 시세의 0.6배를 넘으면 보증보험 가입이 불가하고
        - 말소되지 않은 근저당의 총합과 보증금의 합이 시세의 0.9배를 넘으면 보증보험 가입이 불가하고
        - 이 부동산이 아파트, 주거용 오피스텔, 연립·다세대주택, 단독·다중·다가구주택, 노인복지주택에 해당해야 가입이 가능해
        
        이제 아래 등기부등본 택스트를 분석해서 결과를 다음 JSON 형식으로 응답해줘:
        {
          "dangerNum":"점수총합" 예시 : "-2"
          "address":"표제부에 있는 이 부동산의 주소"
          "summary":"거래에 대한 두줄 요약"
          "safetyDescription":"두줄 요약 판단의 이유 10줄 정도를 공인중개사처럼 부드러운 상담톤으로 해줘"
          "dept":"말소되지 않은 근저당 금액의 총합"
          "insuranceDescription":"위 조건들을 바탕으로 이 부동산이 보증보험 가입이 가능할지 여부와 그 이유를 10줄 정도로 점수나 수치를 언급하지 않고 이해하기 쉽게 말하는 부드러운 상담톤으로 적어주고,
           만약 보증보험 가입이 가능하다면 임대인의 세금 체납과 신용은 고려하지 않은 결과이기에 더 정확한 결과는 직접 주택도시보증공사에서 자세한 상담이 필요하다는 말을 추가해줘"
        }
        """,Math.round(officalPrice*1.3)))); // 형변환 필요
      
    }else if(gptAnalysisRequest.getIsMonthlyRent() == 1) {  // 월세 계약인 경우
      prompts.add(Map.of("role", "user", "content", String.format("""
          이 부동산의 시세는 %d야. 전월세변환율을 적용해서 가공한 보증금은 %d야.
          다음 조건들을 보고 보증보험 가입 가능 여부를 판단해줘:
          - 위에서 매긴 점수총합이 양수가 아니라면 보증보험 가입이 불가하고
          - 전월세변환율 적용 보증금이 7억 이하여야만 보증보험 가입이 가능하고
          - 말소되지 않은 근저당의 총합이 시세의 0.6배를 넘으면 보증보험 가입이 불가하고
          - 말소되지 않은 근저당의 총합과 전월세변환율 적용 보증금의 합이 시세의 0.9배를 넘으면 보증보험 가입이 불가하고
          - 이 부동산이 아파트, 주거용 오피스텔, 연립·다세대주택, 단독·다중·다가구주택, 노인복지주택에 해당해야 가입이 가능해
        
        이제 아래 등기부등본 택스트를 분석해서 결과를 다음 JSON 형식으로 응답해줘:
        {
          "dangerNum":"점수총합" 예시 : "-2"
          "address":"표제부에 있는 이 부동산의 주소"
          "summary":"거래에 대한 두줄 요약"
          "safetyDescription":"두줄 요약 판단의 이유 10줄 정도를 공인중개사처럼 부드러운 상담톤으로 해줘"
          "dept":"말소되지 않은 근저당 금액의 총합"
          "insuranceDescription":"위 조건들을 바탕으로 이 부동산이 보증보험 가입이 가능할지 여부와 그 이유를 10줄 정도로 점수나 수치를 언급하지 않고 이해하기 쉽게 말하는 부드러운 상담톤으로 적어주고,
           만약 보증보험 가입이 가능하다면 임대인의 세금 체납과 신용은 고려하지 않은 결과이기에 더 정확한 결과는 직접 주택도시보증공사에서 자세한 상담이 필요하다는 말을 추가해줘"
        }
        다음은 등기부등본 텍스트야:
        """,Math.round(officalPrice*1.3), gptAnalysisRequest.getMonthlyRent()*12*100/6+gptAnalysisRequest.getDeposit())));
    }
    // gpt에게 추출된 택스트 입력
    prompts.add(Map.of("role", "user", "content", jsonText));


    return prompts;
  }


  // gpt-4o API 불러오는 메소드
  public String callGptAPI(List<Map<String, String>> prompts, String reportId) {

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
      log.info("[GptService] GPT API 요청 시도 : reportId={}",reportId);
      // POST 요청 
      ResponseEntity<Map> response = restTemplate.postForEntity(gptConfig.getUrl(), requestEntity, Map.class);

      if(response.getStatusCode() != HttpStatus.OK){  // 응답코드 200일때만 응답 꺼내기
        log.error("[GptService] GPT API 응답 실패 : reportId={}, httpStatus={}", reportId, response.getStatusCode());
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
        log.warn("[GptService] GPT API 응답 성공했으나 choices 변수 null : reportId={}",reportId);
        throw new CustomException(GptErrorCode.GPT_EMPTY_RESPONSE);
      }

      // message 변수에 gpt 응답이 담김
      Map<String,Object> message = objectMapper.convertValue(choices.get(0).get("message"), new TypeReference<Map<String,Object>>(){});
      String content = ((String) message.get("content")).replaceAll("```json", "").replaceAll("```", "").trim(); // message안에는 role과 content가 있는데 이중 content가 진짜 답변!

      log.info("[GptService] 응답 성공 : reportId={}, content={}", reportId, content);
      return content; // 이게 찐 gpt 응답 텍스트!

    }catch (HttpClientErrorException e) {
      log.error("[GptService] GPT API 클라이언트 오류: reportId={}, {}", reportId, e.getResponseBodyAsString());
      throw new CustomException(GptErrorCode.GPT_INVALID_PROMPT);

    } catch (ResourceAccessException e) {
      log.error("[GptService] GPT API 타임아웃 또는 접근 실패: reportId={}, {}", reportId, e.getMessage());
      throw new CustomException(GptErrorCode.GPT_TIMEOUT);

    } catch (Exception e) {
      log.error("[GptService] GPT API 호출 중 예외 발생: reportId={}, {}", reportId, e.getMessage());
      throw new CustomException(GptErrorCode.GPT_API_CALL_FAILED);
    }
  }



  // gpt-4o API 응답 파싱해서 데이터 가공하고 반환하는 메소드
  public GptResponse parseGptResponse(String content){
    try{
      return objectMapper.readValue(content, GptResponse.class);    // GptResponse 에서 지정한 응답 형식대로 자동 파싱해서 반환
    }catch (JsonProcessingException e){
      throw new CustomException(GptErrorCode.GPT_RESPONSE_PARSING_FAILED);
    }
  }
}

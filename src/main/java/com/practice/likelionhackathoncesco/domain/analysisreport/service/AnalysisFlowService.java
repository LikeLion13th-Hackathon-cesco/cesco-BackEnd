package com.practice.likelionhackathoncesco.domain.analysisreport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.AnalysisReportResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.ProcessingStatus;
import com.practice.likelionhackathoncesco.domain.analysisreport.repository.AnalysisReportRepository;
import com.practice.likelionhackathoncesco.domain.commonfile.service.FileService;
import com.practice.likelionhackathoncesco.domain.user.service.UserPayService;
import com.practice.likelionhackathoncesco.infra.openai.dto.request.GptAnalysisRequest;
import com.practice.likelionhackathoncesco.infra.openai.dto.response.GptResponse;
import com.practice.likelionhackathoncesco.infra.openai.service.GptService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisFlowService {

  private final GptService gptService;
  private final AnalysisReportService analysisReportService;
  private final FileService fileService;
  private final AnalysisReportRepository analysisReportRepository;
  private final UserPayService userPayService;

  // 분석 리포트를 위한 등기부등본 S3 업로드 + DB 저장
  // 미결제 사용자 검사 추가
  @Transactional
  public AnalysisReport uploadDocuments(PathName pathName, MultipartFile file) {

    userPayService.validateReportCreationForUnpaidUser(1L); // 사용자 id 고정

    AnalysisReport savedReport =
        fileService.uploadFile(
            pathName,
            file,
            () -> AnalysisReport.builder().processingStatus(ProcessingStatus.UPLOADED).build(),
            analysisReportRepository,
            null);

    return savedReport;
  }

  public AnalysisReportResponse processAnalysisReport(
      Long reportId, GptAnalysisRequest gptAnalysisRequest) {

    // 프롬프트 제작
    List<Map<String, String>> prompts;

    try {
      prompts = gptService.createPrompt(gptAnalysisRequest, reportId);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      prompts = new ArrayList<>();
    }

    // gpt-4o api 호출
    String content = gptService.callGptAPI(prompts, String.valueOf(reportId));

    // 응답 파싱
    GptResponse gptResponse = gptService.parseGptResponse(content);

    // 분석 리포트 분석 후 DB 업데이트
    AnalysisReportResponse analysisReportResponse =
        analysisReportService.updateAnalysisReport(gptResponse, gptAnalysisRequest, reportId);

    return analysisReportResponse;
  }
}

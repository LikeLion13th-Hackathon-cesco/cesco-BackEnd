package com.practice.likelionhackathoncesco.domain.analysisreport.service;

import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.commonfile.service.FileService;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleFileService {

  private final FileService fileService;

  public FileUploadResponse uploadExampleDocument(PathName pathName, File file) {

    AnalysisReport analysisReport = fileService.uploadFile(pathName, file);

    return FileUploadResponse.builder()
        .reportId(analysisReport.getReportId())
        .fileName(analysisReport.getFileName())
        .processingStatus(analysisReport.getProcessingStatus()) // 엔티티 생성 시 업로드 상태로 생성
        .build();
  }
}

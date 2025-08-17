package com.practice.likelionhackathoncesco.domain.analysisreport.controller;

import com.practice.likelionhackathoncesco.domain.analysisreport.dto.response.FileUploadResponse;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.service.ExampleFileService;
import com.practice.likelionhackathoncesco.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis-reports")
@Tag(name = "Example Analysis Report Upload", description = "예시 등기부등본 업로드 관련 API")
public class ExampleFileUploadController {

  private final ExampleFileService exampleFileService; // 예시 등기부등본 업로드 서비스

  @Operation(
      summary = "예시1 등기부등본 업로드 API",
      description = "예시1 등기부등본 문서를 업로드하고 문서 원본이름과 상태를 리턴하는 API")
  @PostMapping(value = "example-1")
  public ResponseEntity<BaseResponse<FileUploadResponse>> uploadExampleFile1() {
    // 서버에 저장된 PDF 경로 지정 (resources/pdf/example_1.pdf)
    File exampleFile = new File("src/main/resources/pdf/example_1.pdf");

    FileUploadResponse uploadResponse =
        exampleFileService.uploadExampleDocument(PathName.PROPERTYREGISTRY, exampleFile);

    return ResponseEntity.ok(BaseResponse.success("예시1 등기부등본 업로드에 성공했습니다.", uploadResponse));
  }

  @Operation(
      summary = "예시2 등기부등본 업로드 API",
      description = "예시2 등기부등본 문서를 업로드하고 문서 원본이름과 상태를 리턴하는 API")
  @PostMapping(value = "example-2")
  public ResponseEntity<BaseResponse<FileUploadResponse>> uploadExampleFile2() {
    // 서버에 저장된 PDF 경로 지정 (resources/pdf/example_2.pdf)
    File exampleFile = new File("src/main/resources/pdf/example_2.pdf");

    FileUploadResponse uploadResponse =
        exampleFileService.uploadExampleDocument(PathName.PROPERTYREGISTRY, exampleFile);

    return ResponseEntity.ok(BaseResponse.success("예시2 등기부등본 업로드에 성공했습니다.", uploadResponse));
  }

  @Operation(
      summary = "예시3 등기부등본 업로드 API",
      description = "예시3 등기부등본 문서를 업로드하고 문서 원본이름과 상태를 리턴하는 API")
  @PostMapping(value = "example-3")
  public ResponseEntity<BaseResponse<FileUploadResponse>> uploadExampleFile3() {
    // 서버에 저장된 PDF 경로 지정 (resources/pdf/example_3.pdf)
    File exampleFile = new File("src/main/resources/pdf/example_3.pdf");

    FileUploadResponse uploadResponse =
        exampleFileService.uploadExampleDocument(PathName.PROPERTYREGISTRY, exampleFile);

    return ResponseEntity.ok(BaseResponse.success("예시3 등기부등본 업로드에 성공했습니다.", uploadResponse));
  }
}

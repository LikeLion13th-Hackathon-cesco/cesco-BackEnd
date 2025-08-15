package com.practice.likelionhackathoncesco.domain.commonfile.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.AnalysisReport;
import com.practice.likelionhackathoncesco.domain.analysisreport.entity.PathName;
import com.practice.likelionhackathoncesco.domain.analysisreport.exception.AnalysisReportErrorCode;
import com.practice.likelionhackathoncesco.domain.commonfile.BaseFileEntity;
import com.practice.likelionhackathoncesco.domain.user.entity.User;
import com.practice.likelionhackathoncesco.domain.user.exception.UserErrorCode;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import com.practice.likelionhackathoncesco.global.exception.CustomException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

  private final AmazonS3 amazonS3; // AWS SDK에서 제공하는 S3 클라이언트 객체
  private final S3Config s3Config; // 버킷 이름과 경로 등 설정 정보
  private final UserRepository userRepository;

  // 파일을 S3에 업로드 하고 DB에 관련 정보 저장 후 엔티티 반환
  @Transactional
  public <T extends BaseFileEntity, R extends JpaRepository<T,Long>> T uploadFile(
      PathName pathName,
      MultipartFile file,
      // 매개변수는 받지 않고 T타입의 결과만 반환하는 인터페이스 함수(get()메서드 하나만 가짐 -> get()를 호출하여 객체 생성)
      //각 도메인별로 다른 엔티티 타입을 생성할 수 있도록 하는 팩토리 역할
      Supplier<T> entityCreator,
      R repository,
      // T타입의 객체를 받아서 작업을 수행(여기서는 엔티티 별 필드를 추가로 빌드)
      Consumer<T> additionalSetup) {

    User user = userRepository.findByUsername("cesco")
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    validateFile(file); // 파일 유효성 검사

    String originalFilename = file.getOriginalFilename(); // 기존 파일 이름 저장
    if(originalFilename == null || originalFilename.isBlank()) {
      throw new CustomException(AnalysisReportErrorCode.FILE_NOT_FOUND);
    }

    String keyName = createS3Key(pathName, originalFilename); // S3 파일 경로 생성 (경로+이름) -> 객체 key

    uploadToS3(file, keyName); // S3에 업로드

    // 엔티티 생성
    T entity = entityCreator.get();

    // 공통 필드만 FileService에서 설정
    entity.setFileName(originalFilename);
    entity.setS3Key(keyName);
    entity.setUser(user);

    // 엔티티가 생성되고 공통 필드가 생성된 후 실행하는 작업 (있을 경우에만 실행)
    if (additionalSetup != null) {
      additionalSetup.accept(entity);
    }

    T savedEntity = repository.save(entity); // T타입 엔티티 DB저장

    log.info("파일 업로드 성공: fileName={}, entityType={}", originalFilename, entity.getClass().getSimpleName());

    return savedEntity;
  }

  private void uploadToS3(MultipartFile file, String keyName) {
    // 메타 데이터 설정
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize()); // 파일 크기
    metadata.setContentType(file.getContentType()); // 파일 타입 (application/pdf)

    // S3에 파일 업로드
    try {
      amazonS3.putObject(
          new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), metadata));

      log.info("업로드 성공");

    } catch (Exception e) {
      log.error("S3 upload 중 오류 발생", e);
      throw new CustomException(AnalysisReportErrorCode.FILE_SERVER_ERROR);
    }
  }

  // S3에 업로드된 파일 전체 조회 (S3 Url 반환)
  public List<String> getAllS3Files(PathName pathName) {
    String prefix = switch (pathName) {
      case PROPERTYREGISTRY ->s3Config.getPropertyRegistryPath(); // 버킷 내 등기부등본 폴더구조 선택
      case COMPLAINT -> s3Config.getComplaintPath(); // 버킷 내 고소증 관련 폴더구조 선택
      case FRAUDREPORT -> s3Config.getFraudReportPath(); // 버킷 내 신고 등기부등본 관련 폴더구조 선택
    };

    log.info(">>>> S3 prefix: {}", prefix);

    try {
      return amazonS3
          .listObjectsV2(
              new ListObjectsV2Request().withBucketName(s3Config.getBucket()).withPrefix(prefix))
          .getObjectSummaries()
          .stream()
          // 한글, 공백, 득수문자가 url에 포함될 경우 s3가 자동으로 인코딩하여 반환(디코딩 가능)
          .map(obj -> amazonS3.getUrl(s3Config.getBucket(), obj.getKey()).toString())
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("S3 파일 목록 조회 중 오류 발생", e);
      throw new CustomException(AnalysisReportErrorCode.FILE_SERVER_ERROR);
    }
  }

  // 업로드 파일 유효성 검사
  public void validateFile(MultipartFile file) {
    if (file.getSize() > 50 * 1024 * 1024) { // 파일 크기 50MB 이하 업로드 가능
      throw new CustomException(AnalysisReportErrorCode.FILE_SIZE_INVALID);
    }

    String contentType = file.getContentType(); // 파일의 mime타입 반환
    // getcontentType()은 보안상 좋지 않다는 의견 -> 파일 확장자 검사 로직 변경해야할까요?

    if(contentType == null || !contentType.equals("application/pdf")) { // pdf 형식만 허용
      throw new CustomException(AnalysisReportErrorCode.FILE_TYPE_INVALID);
    }
  }

  // S3 파일 경로 생성 (여기서 keyName은 {PathName/원본파일 이름})
  public String createS3Key(PathName pathName, String originalFilename) {
    String basePath = switch (pathName) {
      case PROPERTYREGISTRY ->s3Config.getPropertyRegistryPath(); // 버킷 내 등기부등본 폴더구조 선택
      case COMPLAINT -> s3Config.getComplaintPath(); // 버킷 내 고소증 관련 폴더구조 선택
      case FRAUDREPORT -> s3Config.getFraudReportPath(); // 버킷 내 신고 등기부등본 관련 폴더구조 선택
    };

    String uuid = UUID.randomUUID().toString(); // key 값을 식별 할 uuid 문자열 생성
    String fileExtension = getFileExtension(originalFilename); // 파일 확장자 추출

    return basePath + "/" + uuid + fileExtension; // 고유성은 보장하지만 원본파일 이름 추척이 힘들거 같음

  }

  // 파일 확장자 추출(".pdf"로 추출)
  private String getFileExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "";
    }
    return filename.substring(filename.lastIndexOf("."));
  }

  // 파일이 s3에 존재하는지 s3key 값으로 확인
  private void existFile(String keyName) {
    if (!amazonS3.doesObjectExist(s3Config.getBucket(), keyName)) {
      throw new CustomException(AnalysisReportErrorCode.FILE_NOT_FOUND);
    }
  }

  // 업로드 한 파일 삭제
  @Transactional
  public <T extends BaseFileEntity, R extends JpaRepository<T, Long>> Boolean deleteFile(Long reportId, R repository) {

    // DB에서 등기부등본 조회
    T report = repository.findById(reportId)
        .orElseThrow(() -> new CustomException(AnalysisReportErrorCode.FILE_NOT_FOUND));

    // S3에 파일이 존재하는지 s3key 값으로 확인
    existFile(report.getS3Key());

    try {
      // S3에서 삭제
      amazonS3.deleteObject(new DeleteObjectRequest(s3Config.getBucket(), report.getS3Key()));
      log.info("S3 파일 삭제 성공: {}", report. getFileName());

      // DB에서 레코드 삭제
      repository.delete(report);
      log.info("DB 레코드 삭제 성공: reportId={}, fileName={}", reportId, report.getFileName());

      return true;
    } catch (Exception e) {
      log.error("파일 삭제 중 오류 발생: reportId={}, fileName={}", reportId, report.getFileName(), e);
      throw new CustomException(AnalysisReportErrorCode.FILE_SERVER_ERROR);
    }
  }

}

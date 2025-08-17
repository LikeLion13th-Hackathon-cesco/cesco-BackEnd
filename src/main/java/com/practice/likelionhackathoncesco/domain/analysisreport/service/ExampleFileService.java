package com.practice.likelionhackathoncesco.domain.commonfile.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.practice.likelionhackathoncesco.domain.user.repository.UserRepository;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleFileService {

  private final AmazonS3 amazonS3; // AWS SDK에서 제공하는 S3 클라이언트 객체
  private final S3Config s3Config; // 버킷 이름과 경로 등 설정 정보
  private final UserRepository userRepository;

  public String uploadExamplePdf(String localFilePath, String pathName) {
    File file = new File(localFilePath);

    amazonS3.putObject(new PutObjectRequest(s3Config.getBucket(), pathName, file)
        .withCannedAcl(CannedAccessControlList.PublicRead));

    return amazonS3.getUrl(s3Config.getBucket(), pathName).toString();
  }

}

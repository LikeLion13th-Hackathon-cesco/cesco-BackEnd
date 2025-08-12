package com.practice.likelionhackathoncesco.domain.fraudreport.service;

import com.amazonaws.services.s3.AmazonS3;
import com.practice.likelionhackathoncesco.global.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudReportService { // 사기 신고 서비스 로직

  private final AmazonS3 amazonS3; // AWS SDK에서 제공하는 S3 클라이언트 객체
  private final S3Config s3Config; // 버킷 이름과 경로 등 설정 정보


}

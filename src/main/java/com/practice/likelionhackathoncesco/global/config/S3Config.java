package com.practice.likelionhackathoncesco.global.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class S3Config {

  private AWSCredentials awsCredentials;

  @Value("${cloud.aws.credentials.access-key}")
  private String accessKey; // 엑세스 키 -> 노션에 있음

  @Value("${cloud.aws.credentials.secret-key}")
  private String secretKey; // 비밀 엑세스 키 -> 노션에 있음

  @Value("${cloud.aws.region.static}")
  private String region; // AWS 리전 -> 서울

  @Value("${cloud.aws.s3.bucket}")
  private String bucket; // S3 버킷 이름 -> serbangsari

  @Value("${cloud.aws.s3.path.propertyRegistry}")
  private String propertyRegistryPath; // 버킷 내 등기부등본 관련 저장 경로(폴더 구조)

  @Value("${cloud.aws.s3.path.complaint}")
  private String complaintPath; // 버킷 내 고소증 관련 저장 경로(폴더 구조)

  @Value("${cloud.aws.s3.path.fraudReport}")
  private String fraudReportPath; // 버킷 내 신고 등기부등본 관련 저장 경로(폴더 구조)

  @PostConstruct
  public void init() {
    this.awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
  }

  @Bean
  public AmazonS3 amazonS3() {
    AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonS3ClientBuilder.standard()
        .withRegion(region)
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .build();
  }

  @Bean
  public AWSCredentialsProvider awsCredentialsProvider() {
    return new AWSStaticCredentialsProvider(awsCredentials);
  }
}
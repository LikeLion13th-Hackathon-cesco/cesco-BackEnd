package com.practice.likelionhackathoncesco.naverocr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
// 공식 문서 기준 이미지 관련 내용
/*
이미지 형식
jpg | jpeg | png | pdf | tif | tiff
pdf: 최대 10 페이지 인식 가능
*/
public class ImageDto {

  @JsonProperty("format")
  @Builder.Default
  private String format = "pdf"; // PDF 고정

  @JsonProperty("name")
  private String name; // 파일명

  @JsonProperty("data")
  // 이미지 url (s3에 업로드된 pdf파일의 객체 url로 전송 -> 보안정책기에서 public으로 변경하여 누구나 접근 가능)
  private String url;

}

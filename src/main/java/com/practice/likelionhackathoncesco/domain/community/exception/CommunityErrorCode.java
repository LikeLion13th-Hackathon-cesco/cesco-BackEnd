package com.practice.likelionhackathoncesco.domain.community.exception;

import com.practice.likelionhackathoncesco.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommunityErrorCode implements BaseErrorCode {

  COMMUNITY_NOT_FOUND("COMMUNITY_4001", "해당 커뮤니티를 찾을 수 없습니다", HttpStatus.NOT_FOUND);
  private final String code;
  private final String message;
  private final HttpStatus Status;
}

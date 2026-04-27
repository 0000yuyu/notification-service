package com.yeoljeong.tripmate.notification.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum NotificationHistoryErrorCode implements ErrorCode {
  FAIL_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "발송 실패일 경우 실패 이유가 필수입니다."),
  INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "payload는 json 타입이여야 합니다."),

  ;
  private final HttpStatus status;
  private final String message;

  @Override
  public int getCode() {
    return this.status.value();
  }
}

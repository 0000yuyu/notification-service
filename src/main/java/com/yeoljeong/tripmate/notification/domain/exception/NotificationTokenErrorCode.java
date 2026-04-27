package com.yeoljeong.tripmate.notification.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum NotificationTokenErrorCode implements ErrorCode {

  PUSH_DEVICE_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "푸시 알림은 기기 정보가 필수입니다."),
  EMAIL_MISMATCHED(HttpStatus.BAD_REQUEST, "이메일 형식에 맞지 않습니다."),
  INVALID_MAX_COUNT(HttpStatus.BAD_REQUEST, "최대 실패 횟수는 1이상 이여야 합니다."),
  ;

  private final HttpStatus status;
  private final String message;

  @Override
  public int getCode() {
    return this.status.value();
  }
}

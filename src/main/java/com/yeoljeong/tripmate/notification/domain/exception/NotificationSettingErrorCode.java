package com.yeoljeong.tripmate.notification.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum NotificationSettingErrorCode implements ErrorCode {
  ;
  private final HttpStatus status;
  private final String message;

  @Override
  public int getCode() {
    return this.status.value();
  }
}

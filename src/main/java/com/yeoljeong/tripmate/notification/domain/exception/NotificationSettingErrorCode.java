package com.yeoljeong.tripmate.notification.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum NotificationSettingErrorCode implements ErrorCode {
  USER_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저의 알림 세팅 정보가 설정되지 않았습니다."),
  ;
  private final HttpStatus status;
  private final String message;
  
  @Override
  public int getCode() {
    return this.status.value();
  }
}

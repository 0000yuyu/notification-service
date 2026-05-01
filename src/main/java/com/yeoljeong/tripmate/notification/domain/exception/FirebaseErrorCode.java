package com.yeoljeong.tripmate.notification.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum FirebaseErrorCode implements ErrorCode {
  FIREBASE_INIT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Firebase SDK 초기화에 실패하였습니다."),

  ;
  private final HttpStatus status;
  private final String message;

  @Override
  public int getCode() {
    return this.status.value();
  }
}

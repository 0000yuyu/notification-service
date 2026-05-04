package com.yeoljeong.tripmate.notification.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum NotificationSendErrorCode implements ErrorCode {
  FIREBASE_INIT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Firebase SDK 초기화에 실패하였습니다."),
  EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "만료되었거나 유효하지 않은 푸시 토큰입니다."),
  INVALID_NOTIFICATION_FORMAT(HttpStatus.BAD_REQUEST, "알림 메시지 형식이 잘못되었습니다."),
  RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "알림 발송 한도를 초과했습니다. 잠시 후 다시 시도해주세요."),
  FCM_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 서버 일시적 오류로 알림 전송에 실패했습니다."),
  INTERNAL_NOTIFICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알림 시스템 내부 오류가 발생했습니다."),
  FAILED_LOAD_TEMPLATE(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "알림 발송에 필요한 템플릿을 조립하는 데 실패했습니다."),
  ;
  private final HttpStatus status;
  private final String message;

  @Override
  public int getCode() {
    return this.status.value();
  }
}

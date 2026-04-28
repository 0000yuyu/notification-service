package com.yeoljeong.tripmate.notification.domain.model;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.domain.constants.NotificationResultStatus;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationHistoryErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationResult {

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_result_status", nullable = false)
  private NotificationResultStatus status;
  @Column(nullable = false)
  private int retryCount;
  private String failReason;

  private NotificationResult(NotificationResultStatus status, int retryCount, String failReason) {
    this.retryCount = retryCount;
    this.status = status;
    this.failReason = failReason;
  }

  public static NotificationResult pending() {
    return new NotificationResult(NotificationResultStatus.PENDING, 0, null);
  }

  public static NotificationResult sent() {
    return new NotificationResult(NotificationResultStatus.SEND, 0, null);
  }

  public NotificationResult markFailure(int MAX_RETRY_COUNT, String failReason) {
    int next = this.retryCount + 1;
    if (failReason == null) {
      throw new BusinessException(NotificationHistoryErrorCode.FAIL_REASON_REQUIRED);
    }
    if (next >= MAX_RETRY_COUNT) {
      return new NotificationResult(NotificationResultStatus.GIVE_UP, next, failReason);
    }
    return new NotificationResult(NotificationResultStatus.FAILED, this.retryCount + 1, failReason);
  }

  protected boolean isFailed() {
    return this.status == NotificationResultStatus.FAILED;
  }
}

package com.yeoljeong.tripmate.notification.application.dto.result;

import lombok.Builder;

@Builder
public record NotificationIndividualResult(
    int index,
    boolean isSuccess,
    String errorMessage
) {

  public static NotificationIndividualResult success() {
    return NotificationIndividualResult.builder()
        .isSuccess(true).build();
  }

  public static NotificationIndividualResult fail(String errorMessage) {
    return NotificationIndividualResult.builder()
        .isSuccess(false)
        .errorMessage(errorMessage)
        .build();
  }
}

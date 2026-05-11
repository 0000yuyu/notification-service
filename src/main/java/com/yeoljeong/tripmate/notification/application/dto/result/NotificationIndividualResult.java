package com.yeoljeong.tripmate.notification.application.dto.result;

import lombok.Builder;

@Builder
public record NotificationIndividualResult(
    int index,
    boolean isSuccess,
    String errorMessage
) {

  public static NotificationIndividualResult success(int index) {
    return NotificationIndividualResult.builder()
        .index(index)
        .isSuccess(true).build();
  }

  public static NotificationIndividualResult fail(int index, String errorMessage) {
    return NotificationIndividualResult.builder()
        .index(index)
        .isSuccess(false)
        .errorMessage(errorMessage)
        .build();
  }
}

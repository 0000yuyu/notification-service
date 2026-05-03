package com.yeoljeong.tripmate.notification.presentation.dto.response;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationIndividualResult;
import lombok.Builder;

@Builder
public record NotificationSendIndividualResponse(
    int index,
    boolean isSuccess,
    String errorMessage
) {

  public static NotificationSendIndividualResponse from(NotificationIndividualResult result) {
    return NotificationSendIndividualResponse.builder()
        .index(result.index())
        .isSuccess(result.isSuccess())
        .errorMessage(result.errorMessage()).build();
  }
}

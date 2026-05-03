package com.yeoljeong.tripmate.notification.presentation.dto.response;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationSendResult;
import java.util.List;
import lombok.Builder;

@Builder
public record NotificationSendResponse(
    List<NotificationSendIndividualResponse> responses
) {

  public static NotificationSendResponse from(NotificationSendResult result) {
    return NotificationSendResponse.builder()
        .responses(
            result.results().stream().map(NotificationSendIndividualResponse::from).toList())
        .build();
  }
}

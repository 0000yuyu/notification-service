package com.yeoljeong.tripmate.notification.application.dto.result;

import java.util.List;
import lombok.Builder;

@Builder
public record NotificationSendResult(List<NotificationIndividualResult> results) {

  public static NotificationSendResult from(List<NotificationIndividualResult> results) {
    return NotificationSendResult.builder().results(results).build();
  }
}


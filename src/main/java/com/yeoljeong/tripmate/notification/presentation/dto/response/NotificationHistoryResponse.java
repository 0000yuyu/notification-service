package com.yeoljeong.tripmate.notification.presentation.dto.response;

import com.yeoljeong.tripmate.notification.application.dto.result.NotificationHistoryIndividualResult;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record NotificationHistoryResponse(
    Page<NotificationHistoryIndividualResponse> histories
) {

  public static NotificationHistoryResponse from(
      Page<NotificationHistoryIndividualResult> results) {
    return NotificationHistoryResponse.builder()
        .histories(results.map(NotificationHistoryIndividualResponse::from))
        .build();
  }
}

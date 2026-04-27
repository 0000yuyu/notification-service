package com.yeoljeong.tripmate.notification.domain.entity;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationHistoryErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationPayload {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Column(nullable = false)
  private String payload;

  public NotificationPayload(String payload) {
    validate(payload);
  }

  private void validate(String payload) {
    try {
      objectMapper.readTree(payload);
    } catch (JacksonException e) {
      throw new BusinessException(NotificationHistoryErrorCode.INVALID_JSON_FORMAT);
    }
  }
}

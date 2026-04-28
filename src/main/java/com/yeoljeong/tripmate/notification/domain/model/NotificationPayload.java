package com.yeoljeong.tripmate.notification.domain.model;

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

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Column(nullable = false)
  private String payload;

  public NotificationPayload(String payload) {
    validate(payload);
    this.payload = payload;
  }

  private void validate(String payload) {
    try {
      OBJECT_MAPPER.readTree(payload);
    } catch (JacksonException e) {
      throw new BusinessException(NotificationHistoryErrorCode.INVALID_JSON_FORMAT);
    }
  }
}

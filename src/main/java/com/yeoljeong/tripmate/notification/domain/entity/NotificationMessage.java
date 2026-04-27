package com.yeoljeong.tripmate.notification.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class NotificationMessage {

  private String title;
  private String body;
  private String redirectUrl;

  private NotificationMessage(String title, String body, String redirectUrl) {
    this.title = title;
    this.body = body;
    this.redirectUrl = redirectUrl;
  }

  public static NotificationMessageBuilder create() {
    return NotificationMessage.builder();
  }
}
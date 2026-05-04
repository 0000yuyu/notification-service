package com.yeoljeong.tripmate.notification.application.dto.result;

import lombok.Builder;

@Builder
public record TemplateMessageResult(
    String title,
    String body
) {

  public static TemplateMessageResult of(String title, String body) {
    return TemplateMessageResult.builder().title(title).body(body).build();
  }

}

package com.yeoljeong.tripmate.notification.infrastructure.dto;

import lombok.Builder;

@Builder
public record TemplateMessage(
    String title,
    String body
) {

}

package com.yeoljeong.tripmate.notification.application.dto.command;

import lombok.Builder;

@Builder
public record NotificationSendTarget(

    String token,
    String title,
    String body
) {

}

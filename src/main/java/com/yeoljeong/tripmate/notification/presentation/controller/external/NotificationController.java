package com.yeoljeong.tripmate.notification.presentation.controller.external;

import com.yeoljeong.tripmate.auth.LoginUser;
import com.yeoljeong.tripmate.auth.UserContext;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationCommandService;
import com.yeoljeong.tripmate.notification.presentation.dto.request.NotificationTokenRequest;
import com.yeoljeong.tripmate.notification.presentation.dto.response.NotificationTokenResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationCommandService notificationCommandService;

  @PostMapping("/tokens/me")
  public ApiResponse<NotificationTokenResponse> registerTokenData(
      @LoginUser UserContext userContext,
      @Validated @RequestBody NotificationTokenRequest request
  ) {
    return ApiResponse.success(
        CommonSuccessCode.OK,
        NotificationTokenResponse.from(
            notificationCommandService.registerTokenData(request.toCommand(
                UUID.fromString(userContext.userId())))
        )
    );
  }
}

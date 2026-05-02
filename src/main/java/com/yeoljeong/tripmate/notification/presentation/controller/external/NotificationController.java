package com.yeoljeong.tripmate.notification.presentation.controller.external;

import com.yeoljeong.tripmate.auth.LoginUser;
import com.yeoljeong.tripmate.auth.UserContext;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationCommandService;
import com.yeoljeong.tripmate.notification.application.service.query.NotificationQueryService;
import com.yeoljeong.tripmate.notification.presentation.dto.request.NotificationSettingRequest;
import com.yeoljeong.tripmate.notification.presentation.dto.request.NotificationTokenRequest;
import com.yeoljeong.tripmate.notification.presentation.dto.response.NotificationSettingResponse;
import com.yeoljeong.tripmate.notification.presentation.dto.response.NotificationTokenResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationCommandService notificationCommandService;
  private final NotificationQueryService notificationQueryService;

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

  @GetMapping("/settings/me")
  public ApiResponse<NotificationSettingResponse> getSettingData(
      @LoginUser UserContext userContext
  ) {
    return ApiResponse.success(CommonSuccessCode.OK,
        NotificationSettingResponse.from(
            notificationQueryService.getSettingData(UUID.fromString(userContext.userId()))
        ));
  }

  @PatchMapping("/settings/me")
  public ApiResponse<NotificationSettingResponse> updateSettingData(
      @LoginUser UserContext userContext,
      @RequestBody @Validated NotificationSettingRequest request
  ) {
    return ApiResponse.success(CommonSuccessCode.OK,
        NotificationSettingResponse.from(
            notificationCommandService.updateSettingData(
                request.toCommand(UUID.fromString(userContext.userId()))
            )
        ));
  }


}

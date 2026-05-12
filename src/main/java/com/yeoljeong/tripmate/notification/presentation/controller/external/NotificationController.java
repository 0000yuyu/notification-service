package com.yeoljeong.tripmate.notification.presentation.controller.external;

import com.yeoljeong.tripmate.auth.annotation.LoginUser;
import com.yeoljeong.tripmate.auth.context.UserContext;
import com.yeoljeong.tripmate.notification.application.service.command.NotificationCommandService;
import com.yeoljeong.tripmate.notification.application.service.query.NotificationQueryService;
import com.yeoljeong.tripmate.notification.presentation.dto.request.NotificationAdminSendRequest;
import com.yeoljeong.tripmate.notification.presentation.dto.request.NotificationHistorySearchByUserRequest;
import com.yeoljeong.tripmate.notification.presentation.dto.request.NotificationSettingRequest;
import com.yeoljeong.tripmate.notification.presentation.dto.request.NotificationTokenRequest;
import com.yeoljeong.tripmate.notification.presentation.dto.response.NotificationHistoryResponse;
import com.yeoljeong.tripmate.notification.presentation.dto.response.NotificationReadUpdateStatusResponse;
import com.yeoljeong.tripmate.notification.presentation.dto.response.NotificationSendResponse;
import com.yeoljeong.tripmate.notification.presentation.dto.response.NotificationSettingResponse;
import com.yeoljeong.tripmate.notification.presentation.dto.response.NotificationTokenResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            notificationCommandService.registerTokenData(request.toCommand(userContext.userId())
            )
        ));
  }

  @GetMapping("/settings/me")
  public ApiResponse<NotificationSettingResponse> getSettingData(
      @LoginUser UserContext userContext
  ) {
    return ApiResponse.success(CommonSuccessCode.OK,
        NotificationSettingResponse.from(
            notificationQueryService.getSettingData(userContext.userId())
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
                request.toCommand(userContext.userId())
            )
        ));
  }

  @GetMapping("/me")
  public ApiResponse<NotificationHistoryResponse> getHistoryData(
      @LoginUser UserContext userContext,
      @ModelAttribute NotificationHistorySearchByUserRequest request,
      @PageableDefault Pageable pageable
  ) {
    return ApiResponse.success(
        CommonSuccessCode.OK,
        NotificationHistoryResponse.from(
            notificationQueryService.getNotificationsByCondition(
                request.toCondition(userContext.userId(), pageable)
            ))
    );
  }

  @PostMapping("/admin/send")
  public ApiResponse<NotificationSendResponse> adminSendNotification(
      @RequestBody @Validated NotificationAdminSendRequest notificationSendRequest
  ) {
    return ApiResponse.success(
        CommonSuccessCode.OK,
        NotificationSendResponse.from(
            notificationCommandService.sendNotificationByAdmin(
                notificationSendRequest.toCommand()
            )
        )
    );
  }

  @PatchMapping("/{notification_history_id}/read")
  public ApiResponse<NotificationReadUpdateStatusResponse> updateReadStatus(
      @LoginUser UserContext userContext,
      @PathVariable UUID notification_history_id
  ) {
    return ApiResponse.success(
        CommonSuccessCode.OK,
        NotificationReadUpdateStatusResponse.from(
            notificationCommandService.updateReadStatus(
                userContext.userId(), notification_history_id
            )
        )
    );
  }

  @PatchMapping("/read-all")
  public ApiResponse<Void> updateAllReadStatus(
      @LoginUser UserContext userContext
  ) {
    notificationCommandService.updateAllReadStatus(userContext.userId());
    return ApiResponse.success(
        CommonSuccessCode.OK, null
    );
  }
}

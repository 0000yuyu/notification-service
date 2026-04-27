package com.yeoljeong.tripmate.notification.domain.entity;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.constants.DeviceType;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationTokenErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
class NotificationEndPoint {

  private static final String EMAIL_PATTERN =
      "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
          "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
  private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ChannelType channelType;

  @Column()
  @Enumerated(EnumType.STRING)
  private DeviceType deviceType;

  @Column(nullable = false)
  private String deviceId;

  @Column(nullable = false)
  private String tokenValue;

  @Builder
  private NotificationEndPoint(ChannelType channelType, String deviceId, DeviceType deviceType,
      String tokenValue) {
    this.channelType = channelType;
    this.deviceType = deviceType;
    this.deviceId = deviceId;
    this.tokenValue = tokenValue;
  }

  public static NotificationEndPoint create
      (ChannelType channelType, DeviceType deviceType, String deviceId, String tokenValue) {
    if (validate(channelType, deviceId, deviceType, tokenValue)) {
      return NotificationEndPoint.builder()
          .channelType(channelType)
          .deviceType(deviceType)
          .deviceId(deviceId)
          .tokenValue(tokenValue)
          .build();
    }
    return null;
  }


  private static boolean validate(ChannelType channelType, String deviceId, DeviceType deviceType,
      String tokenValue) {
    if (channelType == ChannelType.PUSH && (deviceType == null || deviceId == null)) {
      throw new BusinessException(NotificationTokenErrorCode.PUSH_DEVICE_TYPE_REQUIRED);
    }
    if (channelType == ChannelType.EMAIL && !pattern.matcher(tokenValue).matches()) {
      throw new BusinessException(NotificationTokenErrorCode.EMAIL_MISMATCHED);
    }
    return true;
  }
}

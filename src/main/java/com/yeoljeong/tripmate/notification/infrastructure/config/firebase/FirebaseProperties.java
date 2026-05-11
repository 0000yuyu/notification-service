package com.yeoljeong.tripmate.notification.infrastructure.config.firebase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

  private final String serviceKey;

  public InputStream getServiceKeyInputStream() {
    if (this.serviceKey == null || this.serviceKey.isBlank()) {
      throw new IllegalStateException("firebase의 key가 비어있습니다.");
    }
    try {

      byte[] decodedBytes = Base64.getDecoder().decode(this.serviceKey);
      return new ByteArrayInputStream(decodedBytes);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("firebase key는 base64 인코딩된 json이여야 합니다.");
    }
  }
}

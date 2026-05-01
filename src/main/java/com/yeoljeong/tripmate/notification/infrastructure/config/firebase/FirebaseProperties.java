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
    byte[] decodedBytes = Base64.getDecoder().decode(this.serviceKey);
    return new ByteArrayInputStream(decodedBytes);
  }
}

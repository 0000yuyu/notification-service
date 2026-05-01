package com.yeoljeong.tripmate.notification.infrastructure.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

  private final FirebaseProperties firebaseProperties;

  @PostConstruct
  public void init() {
    try {
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(
              GoogleCredentials.fromStream(firebaseProperties.getServiceKeyInputStream()))
          .build();

      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
      }
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }
}
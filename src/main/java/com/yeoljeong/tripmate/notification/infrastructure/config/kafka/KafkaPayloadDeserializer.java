package com.yeoljeong.tripmate.notification.infrastructure.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPayloadDeserializer {

  private final ObjectMapper objectMapper;

  public <T> T deserialize(String payload, Class<T> classObj) {
    try {
      return objectMapper.readValue(payload, classObj);
    } catch (Exception e) {
      log.error("[KAFKA] 역직렬화 실패 - class {},", classObj, e);
      throw new RuntimeException(e);
    }
  }
}

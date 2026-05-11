package com.yeoljeong.tripmate.notification.application.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PayloadConverter {

  private final ObjectMapper objectMapper;

  public <T> T deserialize(String payload, Class<T> classObj) {
    try {
      return objectMapper.readValue(payload, classObj);
    } catch (Exception e) {
      log.error("역직렬화 실패 - class {},", classObj, e);
      throw new RuntimeException(e);
    }
  }

  public String serialize(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("직렬화 실패 - class {},", object, e);
      throw new RuntimeException(e);
    }
  }

}

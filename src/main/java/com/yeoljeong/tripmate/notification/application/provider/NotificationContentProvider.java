package com.yeoljeong.tripmate.notification.application.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.notification.application.dto.result.TemplateMessageResult;
import com.yeoljeong.tripmate.notification.domain.constants.ChannelType;
import com.yeoljeong.tripmate.notification.domain.exception.NotificationSendErrorCode;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;


@Component
@RequiredArgsConstructor
public class NotificationContentProvider implements NotificationContentBuilder {

  private static final String TEMPLATE_PREFIX = "classpath:templates/notifications/";
  private static final Logger log = LogManager.getLogger(NotificationContentProvider.class);

  private final ResourceLoader resourceLoader;
  private final ObjectMapper objectMapper;

  @Override
  public TemplateMessageResult build(String topicName, ChannelType channelType,
      JsonNode dataNode) {
    try {
      JsonNode config;
      Resource jsonRes = resourceLoader.getResource(
          TEMPLATE_PREFIX + channelType.name().toLowerCase() + "." + topicName + ".json");

      try (var is = jsonRes.getInputStream()) {
        config = objectMapper.readTree(is);
      }

      String body = (channelType == ChannelType.EMAIL)
          ? loadHtmlTemplate(config.get("templatePath").asText())
          : config.get("content").asText();

      body = replacePlaceholders(body, dataNode);

      return TemplateMessageResult.of(config.get("title").asText(), body);

    } catch (IOException e) {
      log.error("Failed to load notification template: topic={}, channel={}", topicName,
          channelType, e);
      throw new BusinessException(NotificationSendErrorCode.FAILED_LOAD_TEMPLATE);
    }
  }

  private String loadHtmlTemplate(String templatePath) throws IOException {
    Resource htmlRes = resourceLoader.getResource(TEMPLATE_PREFIX + templatePath);
    try (var isr = new InputStreamReader(htmlRes.getInputStream(), StandardCharsets.UTF_8)) {
      return FileCopyUtils.copyToString(isr);
    }
  }

  private String replacePlaceholders(String content, JsonNode dataNode) {
    String result = content;
    Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      result = result.replace("${" + entry.getKey() + "}", entry.getValue().asText());
    }
    return result;
  }
}
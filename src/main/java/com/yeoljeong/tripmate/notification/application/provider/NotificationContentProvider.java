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
import java.util.Map.Entry;
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
  public TemplateMessageResult build(String topicName, ChannelType channelType, JsonNode dataNode) {
    try {
      Resource jsonRes = resourceLoader.getResource(
          TEMPLATE_PREFIX + channelType.name() + "." + topicName + ".json");

      JsonNode config = objectMapper.readTree(jsonRes.getInputStream());

      String body;

      if (channelType == ChannelType.EMAIL) {
        String templatePath = config.get("templatePath").asText();
        Resource htmlRes = resourceLoader.getResource(
            TEMPLATE_PREFIX + templatePath);
        body = FileCopyUtils.copyToString(
            new InputStreamReader(htmlRes.getInputStream(), StandardCharsets.UTF_8));
      } else {
        body = config.get("content").asText();
      }

      Iterator<Entry<String, JsonNode>> fields = dataNode.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> entry = fields.next();
        body = body.replace("${" + entry.getKey() + "}", entry.getValue().asText());
      }
      return TemplateMessageResult.of(
          config.get("title").asText(),
          body
      );
    } catch (IOException e) {
      log.info(e);
      throw new BusinessException(NotificationSendErrorCode.FAILED_LOAD_TEMPLATE);
    }
  }
}
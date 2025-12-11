package io.github.rromanowicz.apicaller.common;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Util {

  public static ObjectMapper OBJECT_MAPPER = objMapper();

  private static ObjectMapper objMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModules(new Jdk8Module(), new JavaTimeModule());
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

  public static <T> String asJsonString(String body, Class<T> valueType) {
    try {
      return OBJECT_MAPPER.writeValueAsString(OBJECT_MAPPER.readValue(body,
          new TypeReference<T>() {
          }));
    } catch (JsonProcessingException e) {
      log.warn("Failed to parse String body [{}] to type [{}].\n{}", body,
          valueType.getTypeName(), e.getMessage());
      return null;
    }
  }

  public static <T> String asJsonString(byte[] body, Class<T> valueType) {
    if (isNull(body) || body.length == 0) {
      return null;
    }
    try {
      return OBJECT_MAPPER.writeValueAsString(OBJECT_MAPPER.readValue(body,
          new TypeReference<T>() {
          }));
    } catch (IOException e) {
      log.warn("Failed to parse byte[] body [{}] to type [{}].\n{}", body,
          valueType.getTypeName(), e.getMessage());
      return null;
    }
  }

  public static String asJsonString(Object body) {
    try {
      return OBJECT_MAPPER.writeValueAsString(body);
    } catch (IOException e) {
      log.warn("Failed to parse Object body [{}] as String.\n{}", body, e.getMessage());
      return null;
    }
  }

  public static <T> T asObject(String body, TypeReference<T> valueType) {
    if (StringUtils.isEmpty(body)) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(body, valueType);
    } catch (JsonProcessingException e) {
      log.warn("Failed to parse String body [{}] to typeRef [{}].\n{}", body,
          valueType.getType().getTypeName(), e.getMessage());
      return null;
    }
  }

  public static <T> T asObject(byte[] body, TypeReference<T> valueType) {
    if (isNull(valueType) || isNull(body) || body.length == 0) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(body, valueType);
    } catch (IOException e) {
      log.warn("Failed to parse byte[] body [{}] to typeRef [{}].\n{}", body,
          valueType.getType().getTypeName(), e.getMessage());
      return null;
    }
  }

}

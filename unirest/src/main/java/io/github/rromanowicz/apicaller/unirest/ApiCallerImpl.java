package io.github.rromanowicz.apicaller.unirest;

import static io.github.rromanowicz.apicaller.common.Util.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.rromanowicz.apicaller.core.ApiCaller;
import java.net.URI;
import java.util.Map;
import kong.unirest.core.RawResponse;
import kong.unirest.core.Unirest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("uniRestCaller")
public class ApiCallerImpl implements ApiCaller {

  @Override
  public <T> T get(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client) {
    return Unirest.get(uri.toString())
        .headers(headers)
        .asObject(rawResponse -> parse(rawResponse, typeRef))
        .getBody();
  }

  @Override
  public <T> T post(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client) {
    return Unirest.post(uri.toString())
        .headers(headers)
        .body(body)
        .asObject(rawResponse -> parse(rawResponse, typeRef))
        .getBody();
  }

  @Override
  public <T> T patch(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client) {
    return null;
  }

  @Override
  public <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client) {
    return null;
  }

  @Override
  public ResponseEntity<Void> delete(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull String client) {
    return null;
  }

  private <T> T parse(RawResponse rawResponse, TypeReference<T> typeReference) {
    try {
      String content = rawResponse.getContentAsString();
      if (StringUtils.isEmpty(content)) {
        return null;
      }
      return OBJECT_MAPPER.readValue(content, typeReference);
    } catch (JsonProcessingException e) {
      log.warn("Failed to process raw response. {}, {}", rawResponse, e.getMessage());
      return null;
    }
  }
}

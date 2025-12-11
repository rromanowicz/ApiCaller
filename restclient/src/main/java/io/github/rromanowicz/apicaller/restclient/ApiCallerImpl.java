package io.github.rromanowicz.apicaller.restclient;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.rromanowicz.apicaller.core.ApiCaller;
import java.net.URI;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component("restClientCaller")
public class ApiCallerImpl implements ApiCaller {

  public static final String RESP_TYPE = "respType";
  public static final String CLIENT_NAME = "clientName";

  private final RestClient restClient;

  @Override
  public <T> T get(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client) {
    return restClient.get()
        .uri(uri)
        .headers(httpHeaders -> httpHeaders.putAll(MultiValueMap.fromSingleValue(headers)))
        .attributes(attrs -> attrs.putAll(Map.of(RESP_TYPE, typeRef, CLIENT_NAME, client)))
        .retrieve()
        .body(ParameterizedTypeReference.forType(typeRef.getType()));
  }

  @Override
  public <T> T post(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client) {
    return restClient.post()
        .uri(uri)
        .body(body)
        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
        .attributes(attrs -> attrs.putAll(Map.of(RESP_TYPE, typeRef, CLIENT_NAME, client)))
        .retrieve()
        .body(ParameterizedTypeReference.forType(typeRef.getType()));
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

}

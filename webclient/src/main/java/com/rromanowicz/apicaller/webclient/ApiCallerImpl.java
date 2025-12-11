package com.rromanowicz.apicaller.webclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rromanowicz.apicaller.core.ApiCaller;
import java.net.URI;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("webClientCaller")
public class ApiCallerImpl implements ApiCaller {

  @Override
  public <T> T get(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client) {
    return null;
  }

  @Override
  public <T> T post(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client) {
    return null;
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

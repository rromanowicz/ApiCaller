package io.github.rromanowicz.apicaller.core;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public interface ApiCaller {

  <T> T get(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client);

  <T> T post(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client);

  <T> T patch(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client);

  <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client);

  ResponseEntity<Void> delete(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull String client);

  default <T> T get(@NonNull URI uri, @NonNull TypeReference<T> typeRef, @NonNull String client) {
    return get(uri, Collections.emptyMap(), typeRef, client);
  }

  default <T> T post(@NonNull URI uri, @NonNull Object body, @NonNull TypeReference<T> typeRef,
      @NonNull String client) {
    return post(uri, body, Collections.emptyMap(), typeRef, client);
  }

  default <T> T patch(@NonNull URI uri, @NonNull Object body, TypeReference<T> typeRef,
      @NonNull String client) {
    return patch(uri, body, Collections.emptyMap(), typeRef, client);
  }

  default <T> T patch(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull String client) {
    return patch(uri, body, headers, null, client);
  }

  default <T> T patch(@NonNull URI uri, @NonNull Object body, @NonNull String client) {
    return patch(uri, body, Collections.emptyMap(), null, client);
  }

  default <T> T put(@NonNull URI uri, @NonNull Object body, TypeReference<T> typeRef,
      @NonNull String client) {
    return patch(uri, body, Collections.emptyMap(), typeRef, client);
  }

  default <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull String client) {
    return patch(uri, body, headers, null, client);
  }

  default <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull String client) {
    return patch(uri, body, Collections.emptyMap(), null, client);
  }

  default ResponseEntity<Void> delete(@NonNull URI uri, @NonNull String client) {
    return delete(uri, Collections.emptyMap(), client);
  }

  default UriComponentsBuilder buildBaseUri(String base, String servicePath) {
    return UriComponentsBuilder.newInstance()
        .uri(URI.create(base))
        .path(servicePath);
  }

  default URI buildUri(String base, String servicePath, @NonNull Map<String, String> queryParams,
      String... pathSegments) {
    var uriBuilder = buildBaseUri(base, servicePath);
    if (nonNull(pathSegments)) {
      for (String seg : pathSegments) {
        uriBuilder.pathSegment(seg);
      }
    }
    if (!queryParams.isEmpty()) {
      queryParams.forEach(uriBuilder::queryParam);
    }
    return uriBuilder.build().toUri();
  }

  default URI buildUri(String base, String servicePath) {
    var uriBuilder = buildBaseUri(base, servicePath);
    return uriBuilder.build().toUri();
  }

}

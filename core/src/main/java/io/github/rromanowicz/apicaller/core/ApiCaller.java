package io.github.rromanowicz.apicaller.core;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Base {@code Caller} interface.
 * </p>
 * Provides default methods to skip not required arguments.
 */
public interface ApiCaller {

  /**
   * GET Call.
   *
   * @param uri     {@code URI} Request uri.
   * @param headers {@code Map<String, String>} Map of headers.
   * @param typeRef {@code TypeReference} Return type reference.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  <T> T get(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client);

  /**
   * POST Call.
   *
   * @param uri     {@code URI} Request uri.
   * @param body    {@code Object} Request errorBody.
   * @param headers {@code Map<String, String>} Map of headers.
   * @param typeRef {@code TypeReference} Return type reference.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  <T> T post(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull TypeReference<T> typeRef, @NonNull String client);

  /**
   * PATCH Call.
   *
   * @param uri     {@code URI} Request uri.
   * @param body    {@code Object} Request errorBody.
   * @param headers {@code Map<String, String>} Map of headers.
   * @param typeRef {@code TypeReference} Return type reference.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  <T> T patch(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client);

  /**
   * PUT Call.
   *
   * @param uri     {@code URI} Request uri.
   * @param headers {@code Map<String, String>} Map of headers.
   * @param typeRef {@code TypeReference} Return type reference.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client);

  /**
   * DELETE Call.
   *
   * @param uri     {@code URI} Request uri.
   * @param headers {@code Map<String, String>} Map of headers.
   * @param client  {@code String} Name of the client from which the method is called.
   * @return {@code ResponseEntity<Void>}
   */
  ResponseEntity<Void> delete(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull String client);

  /**
   * GET Call. Without additional headers.
   *
   * @param uri     {@code URI} Request uri.
   * @param typeRef {@code TypeReference} Return type reference.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  default <T> T get(@NonNull URI uri, @NonNull TypeReference<T> typeRef, @NonNull String client) {
    return get(uri, Collections.emptyMap(), typeRef, client);
  }

  /**
   * POST Call. Without additional headers.
   *
   * @param uri     {@code URI} Request uri.
   * @param body    {@code Object} Request errorBody.
   * @param typeRef {@code TypeReference} Return type reference.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  default <T> T post(@NonNull URI uri, @NonNull Object body, @NonNull TypeReference<T> typeRef,
      @NonNull String client) {
    return post(uri, body, Collections.emptyMap(), typeRef, client);
  }

  /**
   * PATCH Call. Without additional headers.
   *
   * @param uri     {@code URI} Request uri.
   * @param body    {@code Object} Request errorBody.
   * @param typeRef {@code TypeReference} Return type reference.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  default <T> T patch(@NonNull URI uri, @NonNull Object body, TypeReference<T> typeRef,
      @NonNull String client) {
    return patch(uri, body, Collections.emptyMap(), typeRef, client);
  }

  /**
   * PATCH Call. Without return type.
   *
   * @param uri     {@code URI} Request uri.
   * @param body    {@code Object} Request errorBody.
   * @param headers {@code Map<String, String>} Map of headers.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  default <T> T patch(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull String client) {
    return patch(uri, body, headers, null, client);
  }

  /**
   * PATCH Call. Without additional headers and return type.
   *
   * @param uri    {@code URI} Request uri.
   * @param body   {@code Object} Request errorBody.
   * @param client {@code String} Name of the client from which the method is called.
   * @param <T>    Return type.
   * @return Parsed object of type {@code T}
   */
  default <T> T patch(@NonNull URI uri, @NonNull Object body, @NonNull String client) {
    return patch(uri, body, Collections.emptyMap(), null, client);
  }

  /**
   * PUT Call. Without additional headers.
   *
   * @param uri     {@code URI} Request uri.
   * @param typeRef {@code TypeReference} Return type reference.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  default <T> T put(@NonNull URI uri, @NonNull Object body, TypeReference<T> typeRef,
      @NonNull String client) {
    return patch(uri, body, Collections.emptyMap(), typeRef, client);
  }

  /**
   * PUT Call. Without return type.
   *
   * @param uri     {@code URI} Request uri.
   * @param headers {@code Map<String, String>} Map of headers.
   * @param client  {@code String} Name of the client from which the method is called.
   * @param <T>     Return type.
   * @return Parsed object of type {@code T}
   */
  default <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      @NonNull String client) {
    return patch(uri, body, headers, null, client);
  }

  /**
   * PUT Call. Without additional headers and return type.
   *
   * @param uri    {@code URI} Request uri.
   * @param client {@code String} Name of the client from which the method is called.
   * @param <T>    Return type.
   * @return Parsed object of type {@code T}
   */
  default <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull String client) {
    return patch(uri, body, Collections.emptyMap(), null, client);
  }

  /**
   * DELETE Call. Without additional headers.
   *
   * @param uri    {@code URI} Request uri.
   * @param client {@code String} Name of the client from which the method is called.
   * @return {@code ResponseEntity<Void>}
   */
  default ResponseEntity<Void> delete(@NonNull URI uri, @NonNull String client) {
    return delete(uri, Collections.emptyMap(), client);
  }

  /**
   * @param base        Base url.
   * @param servicePath Endpoint path.
   * @return URI builder base.
   */
  default UriComponentsBuilder buildBaseUri(String base, String servicePath) {
    return UriComponentsBuilder.newInstance()
        .uri(URI.create(base))
        .path(servicePath);
  }

  /**
   * @param base         Base url.
   * @param servicePath  Endpoint path.
   * @param queryParams  {@code Map<String, String>} query parameters.
   * @param pathSegments {@code vararg String} path segments.
   * @return Composed URI.
   */
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

  /**
   * @param base        Base url.
   * @param servicePath Endpoint path.
   * @return Composed URI.
   */
  default URI buildUri(String base, String servicePath) {
    var uriBuilder = buildBaseUri(base, servicePath);
    return uriBuilder.build().toUri();
  }

}

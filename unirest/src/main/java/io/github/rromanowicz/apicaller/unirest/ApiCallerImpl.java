package io.github.rromanowicz.apicaller.unirest;

import static io.github.rromanowicz.apicaller.common.Util.OBJECT_MAPPER;
import static io.github.rromanowicz.apicaller.common.Util.asJsonString;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.rromanowicz.apicaller.core.ApiCaller;
import io.github.rromanowicz.apicaller.core.exception.Api3xxException;
import io.github.rromanowicz.apicaller.core.exception.Api4xxException;
import io.github.rromanowicz.apicaller.core.exception.Api5xxException;
import io.github.rromanowicz.apicaller.core.exception.ApiCallerException;
import java.net.URI;
import java.util.Map;
import kong.unirest.core.Empty;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.RawResponse;
import kong.unirest.core.Unirest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
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
    var resp = Unirest.patch(uri.toString())
        .headers(headers)
        .body(body);
    if (nonNull(typeRef)) {
      return resp
          .asObject(rawResponse -> parse(rawResponse, typeRef))
          .getBody();
    } else {
      resp.asEmpty().ifFailure(this::handleErrorResponse);
      return null;
    }
  }

  @Override
  public <T> T put(@NonNull URI uri, @NonNull Object body, @NonNull Map<String, String> headers,
      TypeReference<T> typeRef, @NonNull String client) {
    var resp = Unirest.put(uri.toString())
        .headers(headers)
        .body(body);
    if (nonNull(typeRef)) {
      return resp
          .asObject(rawResponse -> parse(rawResponse, typeRef))
          .getBody();
    } else {
      resp.asEmpty().ifFailure(this::handleErrorResponse);
      return null;
    }
  }

  @Override
  public ResponseEntity<Void> delete(@NonNull URI uri, @NonNull Map<String, String> headers,
      @NonNull String client) {
    var resp = Unirest.post(uri.toString())
        .headers(headers)
        .asEmpty()
        .ifFailure(this::handleErrorResponse);
    return new ResponseEntity<>(HttpStatusCode.valueOf(resp.getStatus()));
  }

  private <T> T parse(RawResponse rawResponse, TypeReference<T> typeReference) {
    String content = rawResponse.getContentAsString();
    handleErrorResponse(rawResponse.getStatus(), content);
    try {
      if (StringUtils.isEmpty(content)) {
        return null;
      }
      return OBJECT_MAPPER.readValue(content, typeReference);
    } catch (JsonProcessingException e) {
      log.warn("Failed to process raw response. {}, {}", rawResponse, e.getMessage());
      return null;
    }
  }

  private void handleErrorResponse(HttpResponse<Empty> emptyHttpResponse) {
    this.handleErrorResponse(emptyHttpResponse.getStatus(), null);
  }

  private void handleErrorResponse(Integer status, String response) {
    switch (status/100) {
      case 2 -> {
      }
      case 3 -> throw new Api3xxException(response);
      case 4 -> throw new Api4xxException(response);
      case 5 -> throw new Api5xxException(response);
      default -> throw new ApiCallerException(response);
    }
  }
}

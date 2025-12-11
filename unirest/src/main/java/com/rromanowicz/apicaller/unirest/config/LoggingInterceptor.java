package com.rromanowicz.apicaller.unirest.config;

import static com.rromanowicz.apicaller.common.Model.Direction.OUTGOING;
import static com.rromanowicz.apicaller.common.Model.Type.REQUEST;
import static com.rromanowicz.apicaller.common.Model.Type.RESPONSE;
import static com.rromanowicz.apicaller.common.Util.OBJECT_MAPPER;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rromanowicz.apicaller.common.MaskingService;
import com.rromanowicz.apicaller.common.Model.ApiHeader;
import com.rromanowicz.apicaller.common.Model.OutgoingLog;
import com.rromanowicz.apicaller.common.Model.RequestLog;
import com.rromanowicz.apicaller.common.Model.ResponseLog;
import kong.unirest.core.Config;
import kong.unirest.core.HttpRequest;
import kong.unirest.core.HttpRequestSummary;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Interceptor;
import kong.unirest.core.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoggingInterceptor implements Interceptor {

  private final MaskingService maskingService;

  @Override
  public void onRequest(HttpRequest<?> request, Config config) {
    Interceptor.super.onRequest(request, config);
  }

  @Override
  public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
    Interceptor.super.onResponse(response, request, config);
    log(request, response);
  }

  @Override
  public HttpResponse<?> onFail(Exception e, HttpRequestSummary request, Config config)
      throws UnirestException {
    return Interceptor.super.onFail(e, request, config);
  }

  private void log(HttpRequestSummary request, HttpResponse<?> response) {
    var outgoingLog = OutgoingLog.builder()
        .request(parseRequest(request))
        .response(parseResponse(response))
        .build();
    log.info(outgoingLog.asJsonString());
  }

  private ResponseLog parseResponse(HttpResponse<?> response) {
    log.debug("[{}] - Processing response body.", response.getRequestSummary().getUrl());
    return ResponseLog.builder()
        .status(response.getStatus())
        .body(maskingService.maskBody(response.getRequestSummary().getUrl(), RESPONSE, OUTGOING,
            response.getBody()))
        .build();
  }

  private RequestLog parseRequest(HttpRequestSummary request) {
    log.debug("[{}] - Processing request body.", request.getUrl());
    String[] mainSplit = request.asString().split("={5,}");
    String parsedRequest = mainSplit.length > 1 ? formatRequestBody(mainSplit[1]) : null;
    if (nonNull(parsedRequest)) {
      try {
        Object value = OBJECT_MAPPER.readValue(parsedRequest, Object.class);
        parsedRequest = OBJECT_MAPPER.writeValueAsString(value);
      } catch (JsonProcessingException e) {
        log.warn("Failed to parse request body as {}. [{}]", parsedRequest, e.getMessage());
      }
    }
    return RequestLog.builder()
        .method(request.getHttpMethod().toString())
        .url(request.getUrl())
        .headers(maskingService.maskHeaders(request.getUrl(),
            request.getHeaders().stream().map(
                it -> ApiHeader.builder().name(it.getName()).value(it.getValue())
                    .build()).toList()))
        .body(maskingService.maskBody(request.getUrl(), REQUEST, OUTGOING, parsedRequest))
        .build();
  }

  private String formatRequestBody(String request) {
    String result = request;
    if (request.startsWith("\n")) {
      result = result.substring(1);
    }
    if (request.endsWith("\n")) {
      result = result.substring(0, result.length() - 1);
    }
    return result.replace("\n", "").replaceAll(" {2,}", " ");
  }

}

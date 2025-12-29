package io.github.rromanowicz.apicaller.unirest.config;

import static io.github.rromanowicz.apicaller.common.Model.Direction.OUTGOING;
import static io.github.rromanowicz.apicaller.common.Model.Type.REQUEST;
import static io.github.rromanowicz.apicaller.common.Model.Type.RESPONSE;
import static io.github.rromanowicz.apicaller.common.Util.OBJECT_MAPPER;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.rromanowicz.apicaller.common.MaskingService;
import io.github.rromanowicz.apicaller.common.Model.ApiHeader;
import io.github.rromanowicz.apicaller.common.Model.ErrorResponse;
import io.github.rromanowicz.apicaller.common.Model.OutgoingLog;
import io.github.rromanowicz.apicaller.common.Model.RequestLog;
import io.github.rromanowicz.apicaller.common.Model.ResponseLog;
import kong.unirest.core.Config;
import kong.unirest.core.HttpRequest;
import kong.unirest.core.HttpRequestSummary;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Interceptor;
import kong.unirest.core.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;

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
    log.debug("[{}] - Processing response errorBody.", response.getRequestSummary().getUrl());
    var res = ResponseLog.builder()
        .status(response.getStatus());
    if (nonNull(response.getBody())) {
      if (HttpStatusCode.valueOf(response.getStatus()).is2xxSuccessful()) {
        res.body(maskingService.maskBody(response.getRequestSummary().getUrl(), RESPONSE, OUTGOING,
            response.getBody()));
      } else {
        res.body(ErrorResponse.builder().errorBody(response.getBody().toString()).build());
      }
    }
    return res.build();
  }

  private RequestLog parseRequest(HttpRequestSummary request) {
    log.debug("[{}] - Processing request errorBody.", request.getUrl());
    String[] mainSplit = request.asString().split("={5,}");
    String parsedRequest = mainSplit.length > 1 ? formatRequestBody(mainSplit[1]) : null;
    if (nonNull(parsedRequest)) {
      try {
        Object value = OBJECT_MAPPER.readValue(parsedRequest, Object.class);
        parsedRequest = OBJECT_MAPPER.writeValueAsString(value);
      } catch (JsonProcessingException e) {
        log.warn("Failed to parse request errorBody as {}. [{}]", parsedRequest, e.getMessage());
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

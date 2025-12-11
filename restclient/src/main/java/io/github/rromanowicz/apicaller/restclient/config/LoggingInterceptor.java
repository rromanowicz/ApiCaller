package io.github.rromanowicz.apicaller.restclient.config;

import static io.github.rromanowicz.apicaller.common.Model.Direction.OUTGOING;
import static io.github.rromanowicz.apicaller.common.Model.Type.REQUEST;
import static io.github.rromanowicz.apicaller.common.Model.Type.RESPONSE;
import static io.github.rromanowicz.apicaller.common.Util.asObject;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.rromanowicz.apicaller.common.MaskingService;
import io.github.rromanowicz.apicaller.common.Model.ApiHeader;
import io.github.rromanowicz.apicaller.common.Model.OutgoingLog;
import io.github.rromanowicz.apicaller.common.Model.RequestLog;
import io.github.rromanowicz.apicaller.common.Model.ResponseLog;
import jakarta.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;


@Slf4j
@RequiredArgsConstructor
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

  private final MaskingService maskingService;

  @Override
  @Nonnull
  public ClientHttpResponse intercept(@Nonnull HttpRequest request, @Nonnull byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    var response = execution.execute(request, body);
    byte[] responseBody = response.getBody().readAllBytes();
    log(request, body, response, responseBody);
    return new BufferingClientHttpResponseWrapper(response, responseBody);
  }

  private void log(HttpRequest request, byte[] body, ClientHttpResponse response,
      byte[] responseBody) {
    var outgoingLog = OutgoingLog.builder()
        .request(parseRequest(request, body))
        .response(parseResponse(response, responseBody,
            (TypeReference<?>) request.getAttributes().getOrDefault("respType", null),
            request.getURI().toString()
        ))
        .build();
    log.info(outgoingLog.asJsonString());

  }

  private <T> ResponseLog parseResponse(ClientHttpResponse response, byte[] body,
      TypeReference<T> respType, String requestUri) {
    log.debug("[{}] - Processing response body.", requestUri);
    Integer statusCode;
    try {
      statusCode = response.getStatusCode().value();
    } catch (IOException e) {
      statusCode = null;
    }
    return ResponseLog.builder()
        .status(statusCode)
        .body(maskingService.maskBody(requestUri, RESPONSE, OUTGOING, asObject(body, respType)))
        .build();
  }

  private RequestLog parseRequest(HttpRequest request, byte[] body) {
    log.debug("[{}] - Processing request body.", request.getURI());
    return RequestLog.builder()
        .method(request.getMethod().toString())
        .url(request.getURI().toString())
        .headers(maskingService.maskHeaders(request.getURI().toString(),
            request.getHeaders().toSingleValueMap().entrySet().stream().map(
                it -> ApiHeader.builder().name(it.getKey()).value(it.getValue())
                    .build()).toList()))
        .body(maskingService.maskBody(request.getURI().toString(), REQUEST, OUTGOING, body))
        .build();
  }

  private record BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body)
      implements ClientHttpResponse {

    @Override
    @Nonnull
    public InputStream getBody() {
      return new ByteArrayInputStream(body);
    }

    @Override
    @Nonnull
    public HttpStatusCode getStatusCode() throws IOException {
      return response.getStatusCode();
    }

    @Override
    @Nonnull
    public String getStatusText() throws IOException {
      return response.getStatusCode().toString();
    }

    @Override
    @Nonnull
    public HttpHeaders getHeaders() {
      return response.getHeaders();
    }

    @Override
    public void close() {
      response.close();
    }

  }
}

package io.github.rromanowicz.apicaller.common;

import static io.github.rromanowicz.apicaller.common.Util.asObject;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import org.apache.commons.lang3.reflect.TypeUtils;

public interface Model {

  record Body(String pattern, String substitution) {

  }

  record Header(String name, String replacement, boolean remove) {

  }

  record Request(List<Body> body, List<Header> header) {

  }

  record Response(List<Body> body, List<Header> header) {

  }

  record Config(String url, Request request, Response response) {

  }

  @Builder
  record OutgoingLog(RequestLog request, ResponseLog response) {

    public String asJsonString() {
      return Util.asJsonString(OutgoingLog.builder()
          .request(this.request.asJsonString())
          .response(this.response.asJsonString())
          .build());
    }
  }

  @Builder
  record IncomingLog(RequestLog incomingRequest) {

    public String asJsonString() {
      return Util.asJsonString(
          IncomingLog.builder().incomingRequest(this.incomingRequest.asJsonString()).build()
      );
    }
  }

  @Builder
  record RequestLog(String method, String url, String query, Collection<ApiHeader> headers,
                    Object body) {

    RequestLog asJsonString() {
      return RequestLog.builder()
          .method(this.method)
          .url(this.url)
          .query(this.query)
          .headers(this.headers)
          .body(asObject((String) this.body, new TypeReference<>() {
          }))
          .build();
    }
  }

  @Builder
  record ResponseLog(Integer status, Object body) {

    ResponseLog asJsonString() {
      var log = ResponseLog.builder()
          .status(this.status);
      if (TypeUtils.isInstance(body, ErrorResponse.class)) {
        log.body(body);
      } else {
        log.body(nonNull(body) ? asObject((String) this.body, new TypeReference<>() {
        }) : null);
      }
      return log.build();
    }
  }

  @Builder
  record ErrorResponse(String errorBody) {

  }

  @Builder
  @JsonSerialize(using = ApiHeaderSerializer.class)
  record ApiHeader(String name, String value) {

  }

  enum Type {
    REQUEST, RESPONSE
  }

  enum Direction {
    INCOMING, OUTGOING
  }

}

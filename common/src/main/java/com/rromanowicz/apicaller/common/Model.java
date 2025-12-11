package com.rromanowicz.apicaller.common;

import static com.rromanowicz.apicaller.common.Util.asObject;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collection;
import java.util.List;
import lombok.Builder;

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
      return ResponseLog.builder()
          .status(this.status)
          .body(nonNull(body) ? asObject((String) this.body, new TypeReference<>() {
          }) : null)
          .build();
    }
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

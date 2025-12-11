package com.rromanowicz.apicaller.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rromanowicz.apicaller.core.ApiCaller;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class TestService {

  @Qualifier("uniRestCaller")
  private final ApiCaller uniRestCaller;
  @Qualifier("restClientCaller")
  private final ApiCaller restClientCaller;
  @Value("${app.apis.fakeStore.base}")
  private String url;
  @Value("${app.apis.fakeStore.users}")
  private String users;

  public static final String TEST_CLIENT = "TestClient";

  public List<Model.User> unirestGet() {
    return uniRestCaller.get(uniRestCaller.buildUri(url, users), new TypeReference<>() {
    }, TEST_CLIENT);
  }

  public Model.User unirestGet(Integer id) {
    return uniRestCaller.get(
        uniRestCaller.buildUri(url, users, Collections.emptyMap(), String.valueOf(id)),
        new TypeReference<>() {
        }, TEST_CLIENT);
  }

  public Model.User unirestPost(Model.User request) {
    return uniRestCaller.post(uniRestCaller.buildUri(url, users), request, new TypeReference<>() {
    }, TEST_CLIENT);
  }

  public List<Model.User> restClientGet() {
    return restClientCaller.get(restClientCaller.buildUri(url, users), new TypeReference<>() {
    }, TEST_CLIENT);
  }

  public Model.User restClientGet(Integer id) {
    return restClientCaller.get(
        restClientCaller.buildUri(url, users, Collections.emptyMap(), String.valueOf(id)),
        new TypeReference<>() {
        }, TEST_CLIENT);
  }

  public Model.User restClientPost(Model.User request) {
    return restClientCaller.post(restClientCaller.buildUri(url, users), request,
        new TypeReference<>() {
        }, TEST_CLIENT);
  }
}

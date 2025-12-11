package io.github.rromanowicz.apicaller.webclient.config;

import io.github.rromanowicz.apicaller.common.MaskingService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Autowired
  private Map<String, String> defaultHeaders;
  @Autowired
  private MaskingService maskingService;

  @Bean
  public WebClient webClient(WebClient.Builder builder) {
    return builder
        .defaultHeaders(httpHeaders -> defaultHeaders.forEach(httpHeaders::add))
        .build();
  }


}

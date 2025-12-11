package io.github.rromanowicz.apicaller.restclient.config;

import io.github.rromanowicz.apicaller.common.LoggingConfig;
import io.github.rromanowicz.apicaller.common.MaskingService;
import io.github.rromanowicz.apicaller.core.exception.Api3xxException;
import io.github.rromanowicz.apicaller.core.exception.Api4xxException;
import io.github.rromanowicz.apicaller.core.exception.Api5xxException;
import io.github.rromanowicz.apicaller.core.exception.ApiCallerException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;

@Configuration
public class RestClientConfig {

  @Autowired
  private Map<String, String> defaultHeaders;
  @Autowired
  private LoggingConfig loggingConfig;
  @Autowired
  private MaskingService maskingService;

  @Bean
  public RestClient restClient() {
    var restClient = RestClient.builder()
        .defaultHeaders(httpHeaders -> defaultHeaders.forEach(httpHeaders::add))
        .defaultStatusHandler(status -> !status.is2xxSuccessful(), errorHandler());
    if (loggingConfig.getEnabled().isOutgoing()) {
      restClient.requestInterceptor(new LoggingInterceptor(maskingService));
    }
    return restClient.build();
  }

  private ErrorHandler errorHandler() {
    return (request, response) -> {
      if (response.getStatusCode().is5xxServerError()) {
        throw new Api5xxException(request.getURI().toString());
      } else if (response.getStatusCode().is4xxClientError()) {
        throw new Api4xxException(request.getURI().toString());
      } else if (response.getStatusCode().is3xxRedirection()) {
        throw new Api3xxException(request.getURI().toString());
      } else {
        throw new ApiCallerException(request.getURI().toString());
      }
    };
  }

}

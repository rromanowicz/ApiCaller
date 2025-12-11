package io.github.rromanowicz.apicaller.unirest.config;

import io.github.rromanowicz.apicaller.common.LoggingConfig;
import io.github.rromanowicz.apicaller.common.MaskingService;
import java.util.Map;
import kong.unirest.core.Config;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnirestConfig {

  @Autowired
  private Map<String, String> defaultHeaders;
  @Autowired
  private LoggingConfig loggingConfig;
  @Autowired
  private MaskingService maskingService;


  @Bean
  public Config config() {
    Config config = Unirest.config();
    if (loggingConfig.getEnabled().isOutgoing()) {
      config.interceptor(new LoggingInterceptor(maskingService));
    }
    defaultHeaders.forEach(config::addDefaultHeader);
    return config;
  }

}

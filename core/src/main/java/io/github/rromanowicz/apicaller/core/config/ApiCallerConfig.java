package io.github.rromanowicz.apicaller.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rromanowicz.apicaller.common.LoggingConfig;
import io.github.rromanowicz.apicaller.common.MaskingService;
import io.github.rromanowicz.apicaller.common.Util;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class ApiCallerConfig {

  @Bean
  @ConfigurationProperties(prefix = "app.request.headers")
  public Map<String, String> defaultHeaders() {
    return new HashMap<>();
  }

  @Bean
  @ConfigurationProperties(prefix = "app.logging")
  public LoggingConfig loggingConfig() {
    return new LoggingConfig();
  }

  @Bean
  public MaskingService maskingService() {
    return new MaskingService(loggingConfig());
  }

  @Bean
  public ObjectMapper objectMapper() {
    return Util.OBJECT_MAPPER;
  }
}

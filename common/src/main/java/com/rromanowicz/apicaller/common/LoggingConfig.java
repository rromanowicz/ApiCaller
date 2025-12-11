package com.rromanowicz.apicaller.common;

import java.util.List;
import lombok.Data;

@Data
//@Configuration
//@ConfigurationProperties("app.logging")
public class LoggingConfig {

  private Enabled enabled;
  private PayloadConfig payload;
  private MaskingConfig masking;

  @Data
  public static class Enabled {

    private boolean incoming;
    private boolean outgoing;
  }

  @Data
  public static class PayloadConfig {

    private Payload incoming;
    private Payload outgoing;
  }

  @Data
  public static class Payload {

    private boolean request;
    private boolean response;
  }

  @Data
  public static class MaskingConfig {

    private List<UrlMask> incoming;
    private List<UrlMask> outgoing;
  }

  @Data
  public static class UrlMask {

    private String url;
    private RequestMask request;
    private ResponseMask response;
  }

  @Data
  public static class RequestMask {

    private List<BodyMask> body;
    private List<HeaderMask> header;
  }

  @Data
  public static class ResponseMask {

    private List<BodyMask> body;
  }

  @Data
  public static class BodyMask {

    private String pattern;
    private String substitution;
  }

  @Data
  public static class HeaderMask {

    private String name;
    private String replacement;
    private boolean remove;
  }
}

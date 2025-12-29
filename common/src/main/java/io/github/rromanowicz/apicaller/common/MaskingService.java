package io.github.rromanowicz.apicaller.common;

import static io.github.rromanowicz.apicaller.common.Util.asJsonString;
import static java.util.Objects.isNull;

import io.github.rromanowicz.apicaller.common.LoggingConfig.BodyMask;
import io.github.rromanowicz.apicaller.common.LoggingConfig.HeaderMask;
import io.github.rromanowicz.apicaller.common.LoggingConfig.UrlMask;
import io.github.rromanowicz.apicaller.common.Model.ApiHeader;
import io.github.rromanowicz.apicaller.common.Model.Direction;
import io.github.rromanowicz.apicaller.common.Model.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MaskingService {

  private final LoggingConfig loggingConfig;

  /**
   * Mask errorBody for log entry.
   *
   * @param requestUrl Url of the request to find appropriate mask
   * @param type       Type (Request / Response)
   * @param direction  Direction (Incoming / Outgoing)
   * @param body       {@code Object} errorBody to mask.
   * @return Masked {@code String}
   */
  public String maskBody(String requestUrl, Type type, Direction direction, Object body) {
    if (isNull(body)) {
      return null;
    } else {
      return getConfig(requestUrl, direction)
          .map(it -> switch (type) {
            case REQUEST -> it.getRequest().getBody();
            case RESPONSE -> it.getResponse().getBody();
          })
          .map(it -> switch (body) {
            case Collection c -> applyCollectionMask(c, it);
            case String s -> applyStringMask(s, it);
            case byte[] b -> applyByteArrMask(b, it);
            default -> applyObjectMask(body, it);
          })
          .orElse(writeAsString(body));
    }
  }

  /**
   * Mask headers
   *
   * @param requestUrl Url of the request to find appropriate mask
   * @param headers    Collection of {@code ApiHeader} to mask.
   * @return Masked headers
   * @see ApiHeader
   */
  public List<ApiHeader> maskHeaders(String requestUrl, Collection<ApiHeader> headers) {
    return getOutgoingConfig(requestUrl)
        .map(it -> applyHeaderMask(headers, it.getRequest().getHeader()))
        .orElse(headers.stream().toList());
  }

  private String applyStringMask(String body, Collection<BodyMask> masks) {
    log.debug("Applying mask on String type");
    return maskBody(writeAsString(body), masks);
  }

  private String applyObjectMask(Object body, Collection<BodyMask> masks) {
    log.debug("Applying mask on Object type");
    return maskBody(writeAsString(body), masks);
  }

  private String applyCollectionMask(Collection<Object> body, Collection<BodyMask> masks) {
    log.debug("Applying mask on Collection type");
    return body.stream().map(it -> maskBody(writeAsString(it), masks))
        .toList().toString();
  }

  private String applyByteArrMask(byte[] body, Collection<BodyMask> masks) {
    log.debug("Applying mask on byte[] type");
    if (body != null && body.length > 0) {
      return maskBody(writeAsString(body), masks);
    }
    return null;
  }

  private static String maskBody(String body, Collection<BodyMask> masks) {
    if (isNull(body)) {
      return null;
    }
    String result = body;
    for (BodyMask mask : masks) {
      var pattern = Pattern.compile(mask.getPattern());
      var matcher = pattern.matcher(result);
      if (matcher.find()) {
        result = matcher.replaceAll(mask.getSubstitution());
      }
    }
    if (result.startsWith("\"")) {
      result = result.substring(1);
    }
    if (result.endsWith("\"")) {
      result = StringUtils.chop(result);
    }
    return result;
  }

  private List<ApiHeader> applyHeaderMask(Collection<ApiHeader> headers,
      Collection<HeaderMask> masks) {
    List<ApiHeader> result = new ArrayList<>();
    headers.forEach(header -> masks.stream().filter(mask -> header.name().equals(mask.getName()))
        .findFirst()
        .ifPresentOrElse(it -> {
          if (!it.isRemove()) {
            result.add(ApiHeader.builder().name(header.name()).value(it.getReplacement()).build());
          }
        },
            () -> result.add(header)));
    return result;
  }

  private Optional<UrlMask> getConfig(String url, Direction direction) {
    return switch (direction) {
      case INCOMING -> getIncomingConfig(url);
      case OUTGOING -> getOutgoingConfig(url);
    };
  }

  private Optional<UrlMask> getOutgoingConfig(String url) {
    return this.loggingConfig.getMasking().getOutgoing().stream()
        .filter(it -> url.contains(it.getUrl()))
        .findFirst();
  }

  private Optional<UrlMask> getIncomingConfig(String url) {
    return this.loggingConfig.getMasking().getIncoming().stream()
        .filter(it -> url.contains(it.getUrl()))
        .findFirst();
  }

  private String writeAsString(Object obj) {
    if (TypeUtils.isInstance(obj, String.class)) {
      return asJsonString((String) obj, Object.class);
    }
    if (TypeUtils.isInstance(obj, byte[].class)) {
      return asJsonString((byte[]) obj, Object.class);
    }
    return asJsonString(obj);
  }

}

package com.rromanowicz.apicaller.incomming;

import static com.rromanowicz.apicaller.common.Model.Direction.INCOMING;
import static com.rromanowicz.apicaller.common.Model.Type.REQUEST;

import com.rromanowicz.apicaller.common.LoggingConfig;
import com.rromanowicz.apicaller.common.MaskingService;
import com.rromanowicz.apicaller.common.Model.ApiHeader;
import com.rromanowicz.apicaller.common.Model.IncomingLog;
import com.rromanowicz.apicaller.common.Model.RequestLog;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "IncomingRequestLoggingFilter", urlPatterns = "*")
public class IncomingRequestLoggingFilter extends OncePerRequestFilter {

  private final MaskingService maskingService;
  private final LoggingConfig loggingConfig;

  @Override
  protected void doFilterInternal(@Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain) throws ServletException, IOException {
    if (loggingConfig.getEnabled().isIncoming()) {
      log.debug("[{}] - Processing incoming request,", request.getRequestURI());
      CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(request);

      var headers = StreamSupport.stream(
              Spliterators.spliteratorUnknownSize(request.getHeaderNames().asIterator(),
                  Spliterator.ORDERED), false)
          .map(it -> ApiHeader.builder().name(it).value(request.getHeader(it)).build())
          .toList();

      var incomingLog = IncomingLog.builder()
          .incomingRequest(RequestLog.builder()
              .method(request.getMethod())
              .url(request.getRequestURI())
              .query(request.getQueryString())
              .headers(headers)
              .body(maskingService.maskBody(request.getRequestURI(), REQUEST, INCOMING,
                  cachedHttpServletRequest.getInputStream().readAllBytes()))
              .build())
          .build();
      log.info(incomingLog.asJsonString());

      filterChain.doFilter(cachedHttpServletRequest, response);
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
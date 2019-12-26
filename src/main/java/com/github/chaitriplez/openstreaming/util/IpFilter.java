package com.github.chaitriplez.openstreaming.util;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.filters.RequestFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class IpFilter extends RequestFilter {

  private final Log log = LogFactory.getLog(IpFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    String ip = getIp((HttpServletRequest) request);
    process(ip, request, response, chain);
  }

  @Override
  protected Log getLogger() {
    return log;
  }

  private String getIp(HttpServletRequest request) {
    // https://en.wikipedia.org/wiki/X-Forwarded-For
    String xff = request.getHeader("X-Forwarded-For");
    if (xff == null || xff.length() == 0) {
      return request.getRemoteAddr();
    } else {
      return xff.split(",")[0];
    }
  }
}

package com.fronde.server.config;

import java.io.IOException;
import java.net.InetAddress;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HomePageRedirectValve extends ValveBase {

  @Value("${server.port}")
  private String sslPort;

  @Override
  public void invoke(Request request, Response response) throws IOException, ServletException {
    if ("/".equals(request.getRequestURI()) && "localhost".equals(request.getServerName())) {
      String responseUrl = "https://";
      responseUrl += InetAddress.getLocalHost().getCanonicalHostName();
      if (!"443".equals(sslPort)) {
        responseUrl += ":" + sslPort;
      }
      responseUrl += "/";
      response.sendRedirect(responseUrl);
    } else {
      getNext().invoke(request, response);
    }
  }
}

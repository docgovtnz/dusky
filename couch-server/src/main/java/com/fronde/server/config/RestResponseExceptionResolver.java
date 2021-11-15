package com.fronde.server.config;

import com.fronde.server.CouchServerApplication;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

@Component
public class RestResponseExceptionResolver extends AbstractHandlerExceptionResolver {

  // Using CouchServerApplication because this class is doing generic error handling
  private static final Logger logger = LoggerFactory.getLogger(CouchServerApplication.class);

  @Override
  protected ModelAndView doResolveException(HttpServletRequest request,
      HttpServletResponse response, Object handler, Exception ex) {
    try {
      logger.error("RestResponseExceptionResolver:", ex);
      if (ex instanceof BadCredentialsException) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      } else {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
      }

      return new ModelAndView();
    } catch (IOException ex2) {
      throw new RuntimeException(ex2);
    }
  }
}

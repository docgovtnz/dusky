package com.fronde.server.services.authorization;


import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AccessDeniedExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  public final ResponseEntity<DefaultErrorResponse> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    DefaultErrorResponse response = new DefaultErrorResponse();

    response.setError("Access Denied");
    response.setMessage(ex.getMessage());

    if (request instanceof ServletWebRequest) {
      ServletWebRequest swr = (ServletWebRequest) request;
      HttpServletRequest servletRequest = swr.getNativeRequest(HttpServletRequest.class);
      if (servletRequest != null) {
        response.setPath(servletRequest.getRequestURI());
      }
    }
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setTimestamp(new Date());

    ResponseEntity<DefaultErrorResponse> responseEntity = new ResponseEntity<>(response,
        HttpStatus.FORBIDDEN);
    return responseEntity;
  }
}

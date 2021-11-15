package com.fronde.server.utils;

/**
 * Simple little wrapper around exceptions so they can be converted into JSON without reporting
 * everything in the exception.
 */
public class RestError {

  private final String path;
  private final int status;
  private final Exception ex;

  public RestError(String path, int status, Exception ex) {
    this.path = path;
    this.status = status;
    this.ex = ex;
  }

  public String getPath() {
    return path;
  }

  public int getStatus() {
    return status;
  }

  public String getException() {
    return ex.getClass().getName();
  }

  public String getMessage() {
    return ex.getMessage();
  }

}

package com.fronde.server.services.noranet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple little class for creating a response message
 *
 * @version 1.0
 * @date 16/07/2021
 */
public class NoraNetResponse {

  private final String timestamp;
  private final int status;
  private final String result;
  private final String fileName;
  private final String message;

  public NoraNetResponse(int status, String result, String fileName, String message) {
    this.status = status;
    this.result = result;
    this.fileName = fileName;
    this.message = message;
    this.timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ").format(new Date());
  }

  public String getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getResult() {
    return result;
  }

  public String getFileName() {
    return fileName;
  }

  public String getMessage() {
    return message;
  }
}

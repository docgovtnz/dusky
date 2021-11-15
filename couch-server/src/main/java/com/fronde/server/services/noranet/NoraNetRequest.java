package com.fronde.server.services.noranet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The request content from AWS S3 bucket
 *
 * @version 1.0
 * @date 14/07/2021
 */

public class NoraNetRequest {

  @JsonProperty("fileName")
  private String fileName;
  @JsonProperty("fileData")
  private JsonNode fileData;

  public NoraNetRequest() {
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public JsonNode getFileData() {
    return fileData;
  }

  public void setFileData(JsonNode fileData) {
    this.fileData = fileData;
  }

}

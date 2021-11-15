package com.fronde.server.services.performance;

import com.fronde.server.utils.JsonUtils;

public class TestContext {

  private String baseURL;
  private String jwtToken;
  private JsonUtils jsonUtils;

  public String getBaseURL() {
    return baseURL;
  }

  public void setBaseURL(String baseURL) {
    this.baseURL = baseURL;
  }

  public String getJwtToken() {
    return jwtToken;
  }

  public void setJwtToken(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  public JsonUtils getJsonUtils() {
    return jsonUtils;
  }

  public void setJsonUtils(JsonUtils jsonUtils) {
    this.jsonUtils = jsonUtils;
  }
}

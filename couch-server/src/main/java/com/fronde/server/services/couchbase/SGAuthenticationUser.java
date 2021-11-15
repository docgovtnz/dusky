package com.fronde.server.services.couchbase;

public class SGAuthenticationUser {

  private String sgUsername;
  private String sgPassword;

  public SGAuthenticationUser() {
  }

  public SGAuthenticationUser(String sgUsername, String sgPassword) {
    this.sgUsername = sgUsername;
    this.sgPassword = sgPassword;
  }

  public String getSgUsername() {
    return sgUsername;
  }

  public void setSgUsername(String sgUsername) {
    this.sgUsername = sgUsername;
  }

  public String getSgPassword() {
    return sgPassword;
  }

  public void setSgPassword(String sgPassword) {
    this.sgPassword = sgPassword;
  }
}

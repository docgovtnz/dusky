package com.fronde.server.services.authentication;


public class AuthenticationResponse {

  private String token;

  public AuthenticationResponse() {
    super();
  }

  public AuthenticationResponse(String token) {
    this.setToken(token);
  }

  public String getToken() {
    return this.token;
  }

  public void setToken(String token) {
    this.token = token;
  }

}

package com.fronde.server.services.application;


/**
 *
 */
public class ApplicationStatus {

  private String version;
  private String environment;

  public ApplicationStatus() {
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

}

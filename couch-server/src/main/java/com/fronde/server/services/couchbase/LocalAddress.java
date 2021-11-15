package com.fronde.server.services.couchbase;

/**
 *
 */
public class LocalAddress {

  private String serverMode;
  private String address;

  public LocalAddress() {
  }

  public String getServerMode() {
    return serverMode;
  }

  public void setServerMode(String serverMode) {
    this.serverMode = serverMode;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

}

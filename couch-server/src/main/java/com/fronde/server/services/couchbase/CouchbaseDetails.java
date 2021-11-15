package com.fronde.server.services.couchbase;

public class CouchbaseDetails {

  private String couchbaseHost;
  private int couchbasePort;
  private String couchbaseBucket;
  private String couchbaseUsername;
  private String couchbasePassword;

  public CouchbaseDetails(String couchbaseHost, int couchbasePort, String couchbaseBucket,
      String couchbaseUsername, String couchbasePassword) {
    this.couchbaseHost = couchbaseHost;
    this.couchbasePort = couchbasePort;
    this.couchbaseBucket = couchbaseBucket;
    this.couchbaseUsername = couchbaseUsername;
    this.couchbasePassword = couchbasePassword;
  }

  public CouchbaseDetails() {
  }

  public String getCouchbaseHost() {
    return couchbaseHost;
  }

  public void setCouchbaseHost(String couchbaseHost) {
    this.couchbaseHost = couchbaseHost;
  }

  public int getCouchbasePort() {
    return couchbasePort;
  }

  public void setCouchbasePort(int couchbasePort) {
    this.couchbasePort = couchbasePort;
  }

  public String getCouchbaseBucket() {
    return couchbaseBucket;
  }

  public void setCouchbaseBucket(String couchbaseBucket) {
    this.couchbaseBucket = couchbaseBucket;
  }

  public String getCouchbaseUsername() {
    return couchbaseUsername;
  }

  public void setCouchbaseUsername(String couchbaseUsername) {
    this.couchbaseUsername = couchbaseUsername;
  }

  public String getCouchbasePassword() {
    return couchbasePassword;
  }

  public void setCouchbasePassword(String couchbasePassword) {
    this.couchbasePassword = couchbasePassword;
  }
}

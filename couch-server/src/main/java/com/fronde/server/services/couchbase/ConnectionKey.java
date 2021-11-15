package com.fronde.server.services.couchbase;

public class ConnectionKey {

  private final String local;
  private final String remote;

  public ConnectionKey(String local, String remote) {
    this.local = local;
    this.remote = remote;
  }

  public String getLocal() {
    return local;
  }

  public String getRemote() {
    return remote;
  }

  /**
   * This toString() value is used as the actual Map key instead of the object itself and
   * implementing equals() plus hashcode(), because that makes things easier when converting to
   * JSON.
   *
   * @return
   */
  @Override
  public String toString() {
    return local + " -> " + remote;
  }
}

package com.fronde.server.services.couchbase;


public class SyncGatewayDetails {

  private String bucketIdentifier;
  private String syncGatewayProtocol;
  private String syncGatewayHost;
  private Integer syncGatewayPort;
  private String syncGatewayBucket;
  private String version;
  private String environment;

  // TODO: remove this and checks done with it once release deployed in more places
  private boolean requireAuthentication;

  public SyncGatewayDetails() {
  }

  public String getBucketIdentifier() {
    return bucketIdentifier;
  }

  public void setBucketIdentifier(String bucketIdentifier) {
    this.bucketIdentifier = bucketIdentifier;
  }

  public String getSyncGatewayProtocol() {
    return syncGatewayProtocol;
  }

  public void setSyncGatewayProtocol(String syncGatewayProtocol) {
    this.syncGatewayProtocol = syncGatewayProtocol;
  }

  public String getSyncGatewayHost() {
    return syncGatewayHost;
  }

  public void setSyncGatewayHost(String syncGatewayHost) {
    this.syncGatewayHost = syncGatewayHost;
  }

  public Integer getSyncGatewayPort() {
    return syncGatewayPort;
  }

  public void setSyncGatewayPort(Integer syncGatewayPort) {
    this.syncGatewayPort = syncGatewayPort;
  }

  public String getSyncGatewayBucket() {
    return syncGatewayBucket;
  }

  public void setSyncGatewayBucket(String syncGatewayBucket) {
    this.syncGatewayBucket = syncGatewayBucket;
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

  public boolean isRequireAuthentication() {
    return requireAuthentication;
  }

  public void setRequireAuthentication(boolean requireAuthentication) {
    this.requireAuthentication = requireAuthentication;
  }

  public String getGatewayURL() {
    return getSyncGatewayHost() + ":" + getSyncGatewayPort();
  }

  public String getReplicationUrl(SGAuthenticationUser user) {
    String authentication = "";
    if (user != null && user.getSgUsername() != null && user.getSgPassword() != null) {
      authentication = user.getSgUsername() + ":" + user.getSgPassword() + "@";
    }
    return getSyncGatewayProtocol() + "://" + authentication + getGatewayURL() + "/"
        + getSyncGatewayBucket();
  }

  @Override
  public String toString() {
    return "SyncGatewayDetails{" +
        syncGatewayHost + ":" +
        syncGatewayPort + "/" +
        syncGatewayBucket + "}";
  }
}

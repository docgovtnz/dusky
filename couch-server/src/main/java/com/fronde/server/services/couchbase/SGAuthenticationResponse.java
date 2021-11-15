package com.fronde.server.services.couchbase;

public class SGAuthenticationResponse {

  private SGAuthenticationUser sgAuthenticationUser;
  private SyncGatewayDetails syncGatewayDetails;

  public SGAuthenticationResponse() {
  }

  public SGAuthenticationResponse(SGAuthenticationUser sgAuthenticationUser,
      SyncGatewayDetails syncGatewayDetails) {
    this.sgAuthenticationUser = sgAuthenticationUser;
    this.syncGatewayDetails = syncGatewayDetails;
  }

  public SGAuthenticationUser getSgAuthenticationUser() {
    return sgAuthenticationUser;
  }

  public void setSgAuthenticationUser(SGAuthenticationUser sgAuthenticationUser) {
    this.sgAuthenticationUser = sgAuthenticationUser;
  }

  public SyncGatewayDetails getSyncGatewayDetails() {
    return syncGatewayDetails;
  }

  public void setSyncGatewayDetails(SyncGatewayDetails syncGatewayDetails) {
    this.syncGatewayDetails = syncGatewayDetails;
  }
}

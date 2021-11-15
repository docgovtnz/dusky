package com.fronde.server.services.couchbase;

import java.util.Map;

public class SyncGatewayStatus {

  private String status;
  private String serverMode;
  private Map<String, ReplicationConnection> connectionMap;
  private FileActivity fileActivity;

  public SyncGatewayStatus() {
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getServerMode() {
    return serverMode;
  }

  public void setServerMode(String serverMode) {
    this.serverMode = serverMode;
  }

  public Map<String, ReplicationConnection> getConnectionMap() {
    return connectionMap;
  }

  public void setConnectionMap(Map<String, ReplicationConnection> connectionMap) {
    this.connectionMap = connectionMap;
  }

  public FileActivity getFileActivity() {
    return fileActivity;
  }

  public void setFileActivity(FileActivity fileActivity) {
    this.fileActivity = fileActivity;
  }
}

package com.fronde.server.services.couchbase;

import java.util.ArrayList;
import java.util.List;

public class ReplicationConnection {

  private String local;
  private String remote;

  private String localTaskId;
  private String remoteTaskId;

  private List<ReplicationStats> localActivityList = new ArrayList<>();
  private List<ReplicationStats> remoteActivityList = new ArrayList<>();

  public ReplicationConnection() {
  }

  public String getLocal() {
    return local;
  }

  public void setLocal(String local) {
    this.local = local;
  }

  public String getRemote() {
    return remote;
  }

  public void setRemote(String remote) {
    this.remote = remote;
  }

  public String getLocalTaskId() {
    return localTaskId;
  }

  public void setLocalTaskId(String localTaskId) {
    this.localTaskId = localTaskId;
  }

  public String getRemoteTaskId() {
    return remoteTaskId;
  }

  public void setRemoteTaskId(String remoteTaskId) {
    this.remoteTaskId = remoteTaskId;
  }

  public List<ReplicationStats> getLocalActivityList() {
    return localActivityList;
  }

  public void setLocalActivityList(List<ReplicationStats> localActivityList) {
    this.localActivityList = localActivityList;
  }

  public List<ReplicationStats> getRemoteActivityList() {
    return remoteActivityList;
  }

  public void setRemoteActivityList(List<ReplicationStats> remoteActivityList) {
    this.remoteActivityList = remoteActivityList;
  }
}

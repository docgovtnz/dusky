package com.fronde.server.services.upgrade;

import java.util.ArrayList;
import java.util.List;

public class FileMetaDataList {

  private String cloudVersion;
  private String localVersion;

  private List<FileMetaData> list = new ArrayList<>();

  public FileMetaDataList() {
  }

  public String getCloudVersion() {
    return cloudVersion;
  }

  public void setCloudVersion(String cloudVersion) {
    this.cloudVersion = cloudVersion;
  }

  public String getLocalVersion() {
    return localVersion;
  }

  public void setLocalVersion(String localVersion) {
    this.localVersion = localVersion;
  }

  public List<FileMetaData> getList() {
    return list;
  }

  public void setList(List<FileMetaData> list) {
    this.list = list;
  }
}

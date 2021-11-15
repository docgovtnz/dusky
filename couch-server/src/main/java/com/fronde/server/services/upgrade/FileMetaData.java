package com.fronde.server.services.upgrade;

public class FileMetaData {

  private String name;
  private Long expectedChecksum;
  private Long localChecksum;
  private String status;

  public FileMetaData() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getExpectedChecksum() {
    return expectedChecksum;
  }

  public void setExpectedChecksum(Long expectedChecksum) {
    this.expectedChecksum = expectedChecksum;
  }

  public Long getLocalChecksum() {
    return localChecksum;
  }

  public void setLocalChecksum(Long localChecksum) {
    this.localChecksum = localChecksum;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

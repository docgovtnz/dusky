package com.fronde.server.services.snarkimport;

public class SnarkCheckRequestDTO {

  private String locationID;
  private String snarkFileContent;
  private Integer qualityOverride;
  private boolean showLockRecords;

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getSnarkFileContent() {
    return snarkFileContent;
  }

  public void setSnarkFileContent(String snarkFileContent) {
    this.snarkFileContent = snarkFileContent;
  }

  public Integer getQualityOverride() {
    return qualityOverride;
  }

  public void setQualityOverride(Integer qualityOverride) {
    this.qualityOverride = qualityOverride;
  }

  public boolean isShowLockRecords() { return showLockRecords; }

  public void setShowLockRecords(boolean showLockRecords) {
    this.showLockRecords = showLockRecords;
  }
}

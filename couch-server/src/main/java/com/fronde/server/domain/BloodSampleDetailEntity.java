package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class BloodSampleDetailEntity {

  @Field
  private Float totalBloodVolumeInMl;
  @Field
  private List<String> veinSite;
  @Field
  private List<BloodSampleEntity> bloodSampleList;

  public Float getTotalBloodVolumeInMl() {
    return totalBloodVolumeInMl;
  }

  public void setTotalBloodVolumeInMl(Float totalBloodVolumeInMl) {
    this.totalBloodVolumeInMl = totalBloodVolumeInMl;
  }

  public List<String> getVeinSite() {
    return veinSite;
  }

  public void setVeinSite(List<String> veinSite) {
    this.veinSite = veinSite;
  }

  public List<BloodSampleEntity> getBloodSampleList() {
    return bloodSampleList;
  }

  public void setBloodSampleList(
      List<BloodSampleEntity> bloodSampleList) {
    this.bloodSampleList = bloodSampleList;
  }
}

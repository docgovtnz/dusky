package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class BloodDetailEntity {

  @Field
  private Float totalBloodVolumeInMl;
  @Field
  private String type;
  @Field
  private Float volumeInMl;
  @Field
  private List<String> veinSite;

  public Float getTotalBloodVolumeInMl() {
    return totalBloodVolumeInMl;
  }

  public void setTotalBloodVolumeInMl(Float totalBloodVolumeInMl) {
    this.totalBloodVolumeInMl = totalBloodVolumeInMl;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Float getVolumeInMl() {
    return volumeInMl;
  }

  public void setVolumeInMl(Float volumeInMl) {
    this.volumeInMl = volumeInMl;
  }

  public List<String> getVeinSite() {
    return veinSite;
  }

  public void setVeinSite(List<String> veinSite) {
    this.veinSite = veinSite;
  }

}
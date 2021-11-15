package com.fronde.server.services.matingreport;

import java.util.Objects;

public class MatingKey {

  private final String maleBirdID;
  private final String femailBirdID;
  private final Integer quality;
  private final Integer duration;

  public MatingKey(String maleBirdID, String femailBirdID, Integer quality, Integer duration) {
    this.maleBirdID = maleBirdID;
    this.femailBirdID = femailBirdID;
    this.quality = quality;
    this.duration = duration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MatingKey matingKey = (MatingKey) o;
    return Objects.equals(maleBirdID, matingKey.maleBirdID) &&
        Objects.equals(femailBirdID, matingKey.femailBirdID) &&
        Objects.equals(quality, matingKey.quality) &&
        Objects.equals(duration, matingKey.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maleBirdID, femailBirdID, quality, duration);
  }

}

package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SupplementaryFeedingEntity {

  @Field
  private String feedingRegime;
  @Field
  private List<String> foodTypes;
  @Field
  private String oldFoodType;
  @Field
  private String recoveryId;

  public String getFeedingRegime() {
    return feedingRegime;
  }

  public void setFeedingRegime(String feedingRegime) {
    this.feedingRegime = feedingRegime;
  }

  public List<String> getFoodTypes() {
    return foodTypes;
  }

  public void setFoodTypes(List<String> foodTypes) {
    this.foodTypes = foodTypes;
  }

  public String getOldFoodType() {
    return oldFoodType;
  }

  public void setOldFoodType(String oldFoodType) {
    this.oldFoodType = oldFoodType;
  }

  public String getRecoveryId() {
    return recoveryId;
  }

  public void setRecoveryId(String recoveryId) {
    this.recoveryId = recoveryId;
  }

}
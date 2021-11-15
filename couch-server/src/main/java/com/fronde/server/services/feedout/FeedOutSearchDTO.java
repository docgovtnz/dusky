package com.fronde.server.services.feedout;

import java.util.Date;
import java.util.List;

public class FeedOutSearchDTO {

  private Date dateIn;
  private Date dateOut;
  private String locationID;
  private String locationName;
  private List<FoodTallySearchDTO> foodTallyList;
  private List<TargetBirdSearchDTO> targetBirdList;
  private String feedOutID;

  public Date getDateIn() {
    return dateIn;
  }

  public void setDateIn(Date dateIn) {
    this.dateIn = dateIn;
  }

  public Date getDateOut() {
    return dateOut;
  }

  public void setDateOut(Date dateOut) {
    this.dateOut = dateOut;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public List<FoodTallySearchDTO> getFoodTallyList() {
    return foodTallyList;
  }

  public void setFoodTallyList(List<FoodTallySearchDTO> foodTallyList) {
    this.foodTallyList = foodTallyList;
  }

  public List<TargetBirdSearchDTO> getTargetBirdList() {
    return targetBirdList;
  }

  public void setTargetBirdList(List<TargetBirdSearchDTO> targetBirdList) {
    this.targetBirdList = targetBirdList;
  }

  public String getFeedOutID() {
    return feedOutID;
  }

  public void setFeedOutID(String feedOutID) {
    this.feedOutID = feedOutID;
  }

}

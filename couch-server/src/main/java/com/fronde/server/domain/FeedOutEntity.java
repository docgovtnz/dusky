package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class FeedOutEntity extends BaseEntity {

  @Field
  private String comments;
  @Field
  private Date dateIn;
  @Field
  private Date dateOut;
  @Field
  private String feedId;
  @Field
  private String locationID;
  @Field
  private String newRecord;
  @Field
  private String oldLocation;
  @Field
  private String otherFood;
  @Field
  private Integer otherIn;
  @Field
  private Integer otherOut;
  @Field
  private List<FoodTallyEntity> foodTallyList;
  @Field
  private List<TargetBirdEntity> targetBirdList;

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

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

  public String getFeedId() {
    return feedId;
  }

  public void setFeedId(String feedId) {
    this.feedId = feedId;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getNewRecord() {
    return newRecord;
  }

  public void setNewRecord(String newRecord) {
    this.newRecord = newRecord;
  }

  public String getOldLocation() {
    return oldLocation;
  }

  public void setOldLocation(String oldLocation) {
    this.oldLocation = oldLocation;
  }

  public String getOtherFood() {
    return otherFood;
  }

  public void setOtherFood(String otherFood) {
    this.otherFood = otherFood;
  }

  public Integer getOtherIn() {
    return otherIn;
  }

  public void setOtherIn(Integer otherIn) {
    this.otherIn = otherIn;
  }

  public Integer getOtherOut() {
    return otherOut;
  }

  public void setOtherOut(Integer otherOut) {
    this.otherOut = otherOut;
  }

  public List<FoodTallyEntity> getFoodTallyList() {
    return foodTallyList;
  }

  public void setFoodTallyList(List<FoodTallyEntity> foodTallyList) {
    this.foodTallyList = foodTallyList;
  }

  public List<TargetBirdEntity> getTargetBirdList() {
    return targetBirdList;
  }

  public void setTargetBirdList(List<TargetBirdEntity> targetBirdList) {
    this.targetBirdList = targetBirdList;
  }

}
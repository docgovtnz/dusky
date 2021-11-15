package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class SnarkActivityEntity extends BaseEntity {

  @Field
  private String activityType;
  @Field
  private String comments;
  @Field
  private Date date;
  @Field
  private String locationID;
  @Field
  private String observerPersonID;
  @Field
  private Boolean oldBooming;
  @Field
  private Boolean oldChinging;
  @Field
  private Integer oldFightingSign;
  @Field
  private Integer oldGrubbing;
  @Field
  private Integer oldMatingSign;
  @Field
  private String oldObserver;
  @Field
  private Boolean oldSkraaking;
  @Field
  private Integer oldSticks;
  @Field
  private String oldTAndB;
  @Field
  private String oldTAndBRecId;
  @Field
  private Boolean oldTapeUsed;
  @Field
  private Integer oldTbHopper;
  @Field
  private Integer oldTimeRecorded;
  @Field
  private Integer oldTrackActivity;
  @Field
  private String tandBrecid;
  @Field
  private List<SnarkRecordEntity> snarkRecordList;
  @Field
  private TrackAndBowlActivityEntity trackAndBowlActivity;

  public String getActivityType() {
    return activityType;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getObserverPersonID() {
    return observerPersonID;
  }

  public void setObserverPersonID(String observerPersonID) {
    this.observerPersonID = observerPersonID;
  }

  public Boolean getOldBooming() {
    return oldBooming;
  }

  public void setOldBooming(Boolean oldBooming) {
    this.oldBooming = oldBooming;
  }

  public Boolean getOldChinging() {
    return oldChinging;
  }

  public void setOldChinging(Boolean oldChinging) {
    this.oldChinging = oldChinging;
  }

  public Integer getOldFightingSign() {
    return oldFightingSign;
  }

  public void setOldFightingSign(Integer oldFightingSign) {
    this.oldFightingSign = oldFightingSign;
  }

  public Integer getOldGrubbing() {
    return oldGrubbing;
  }

  public void setOldGrubbing(Integer oldGrubbing) {
    this.oldGrubbing = oldGrubbing;
  }

  public Integer getOldMatingSign() {
    return oldMatingSign;
  }

  public void setOldMatingSign(Integer oldMatingSign) {
    this.oldMatingSign = oldMatingSign;
  }

  public String getOldObserver() {
    return oldObserver;
  }

  public void setOldObserver(String oldObserver) {
    this.oldObserver = oldObserver;
  }

  public Boolean getOldSkraaking() {
    return oldSkraaking;
  }

  public void setOldSkraaking(Boolean oldSkraaking) {
    this.oldSkraaking = oldSkraaking;
  }

  public Integer getOldSticks() {
    return oldSticks;
  }

  public void setOldSticks(Integer oldSticks) {
    this.oldSticks = oldSticks;
  }

  public String getOldTAndB() {
    return oldTAndB;
  }

  public void setOldTAndB(String oldTAndB) {
    this.oldTAndB = oldTAndB;
  }

  public String getOldTAndBRecId() {
    return oldTAndBRecId;
  }

  public void setOldTAndBRecId(String oldTAndBRecId) {
    this.oldTAndBRecId = oldTAndBRecId;
  }

  public Boolean getOldTapeUsed() {
    return oldTapeUsed;
  }

  public void setOldTapeUsed(Boolean oldTapeUsed) {
    this.oldTapeUsed = oldTapeUsed;
  }

  public Integer getOldTbHopper() {
    return oldTbHopper;
  }

  public void setOldTbHopper(Integer oldTbHopper) {
    this.oldTbHopper = oldTbHopper;
  }

  public Integer getOldTimeRecorded() {
    return oldTimeRecorded;
  }

  public void setOldTimeRecorded(Integer oldTimeRecorded) {
    this.oldTimeRecorded = oldTimeRecorded;
  }

  public Integer getOldTrackActivity() {
    return oldTrackActivity;
  }

  public void setOldTrackActivity(Integer oldTrackActivity) {
    this.oldTrackActivity = oldTrackActivity;
  }

  public String getTandBrecid() {
    return tandBrecid;
  }

  public void setTandBrecid(String tandBrecid) {
    this.tandBrecid = tandBrecid;
  }

  public List<SnarkRecordEntity> getSnarkRecordList() {
    return snarkRecordList;
  }

  public void setSnarkRecordList(List<SnarkRecordEntity> snarkRecordList) {
    this.snarkRecordList = snarkRecordList;
  }

  public TrackAndBowlActivityEntity getTrackAndBowlActivity() {
    return trackAndBowlActivity;
  }

  public void setTrackAndBowlActivity(TrackAndBowlActivityEntity trackAndBowlActivity) {
    this.trackAndBowlActivity = trackAndBowlActivity;
  }

}
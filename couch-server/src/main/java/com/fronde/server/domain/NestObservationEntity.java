package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class NestObservationEntity extends BaseEntity {

  @Field
  private Integer activeDayMinutes;
  @Field
  private Integer activeNightMinutes;
  @Field
  private String birdID;
  @Field
  private Integer breathingrate1;
  @Field
  private Integer breathingrate2;
  @Field
  private String chick1id;
  @Field
  private Integer chick1NumberFeeds;
  @Field
  private Integer chick1TimeFed;
  @Field
  private String chick2id;
  @Field
  private Integer chick2NumberFeeds;
  @Field
  private Integer chick2TimeFed;
  @Field
  private Date chickfirstfeed1;
  @Field
  private Date chickfirstfeed2;
  @Field
  private Date chickfirstfeed3;
  @Field
  private Date chickfirstfeed4;
  @Field
  private String comments;
  @Field
  private Date date;
  @Field
  private Date dateTime;
  @Field
  private Date finishObservationTime;
  @Field
  private Date firstTimeOff;
  @Field
  private Date hatchDate;
  @Field
  private Boolean heatpad1;
  @Field
  private Boolean heatpad2;
  @Field
  private Boolean heatpad3;
  @Field
  private Boolean heatpad4;
  @Field
  private Integer heatPadTime;
  @Field
  private Date layDate;
  @Field
  private String locationID;
  @Field
  private Integer maxTimeOff;
  @Field
  private Date mumback1;
  @Field
  private Date mumback2;
  @Field
  private Date mumback3;
  @Field
  private Date mumback4;
  @Field
  private String nestObsId;
  @Field
  private String notes;
  @Field
  private Integer numberOfChicks;
  @Field
  private Integer numberOfEggs;
  @Field
  private Integer numberOfTimesOff;
  @Field
  private String oldBirdId;
  @Field
  private String oldLocationName;
  @Field
  private Float percentageOfDayActive;
  @Field
  private Float percentageOfNightActive;
  @Field
  private Integer rolls;
  @Field
  private Integer scratches;
  @Field
  private Date startObservationTime;
  @Field
  private Integer timeOnNestDuringDaylight;
  @Field
  private Integer timeOnNestDuringNight;
  @Field
  private Integer totalTimeOff;
  @Field
  private Integer unknownChickNumberFeeds;
  @Field
  private Integer unknownChickTimeFed;
  @Field
  private String weighMethod;
  @Field
  private List<ChickRecordReferenceEntity> chickRecordReferenceList;
  @Field
  private List<EggRecordReferenceEntity> eggRecordReferenceList;
  @Field
  private List<MotherTripEntity> motherTripList;
  @Field
  private MotherTripSummaryEntity motherTripSummary;
  @Field
  private NestChamberEntity nestChamber;
  @Field
  private List<NestChickEntity> nestChickList;
  @Field
  private List<NestEggEntity> nestEggList;
  @Field
  private ObservationTimesEntity observationTimes;
  @Field
  private List<ObserverEntity> observerList;

  public Integer getActiveDayMinutes() {
    return activeDayMinutes;
  }

  public void setActiveDayMinutes(Integer activeDayMinutes) {
    this.activeDayMinutes = activeDayMinutes;
  }

  public Integer getActiveNightMinutes() {
    return activeNightMinutes;
  }

  public void setActiveNightMinutes(Integer activeNightMinutes) {
    this.activeNightMinutes = activeNightMinutes;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Integer getBreathingrate1() {
    return breathingrate1;
  }

  public void setBreathingrate1(Integer breathingrate1) {
    this.breathingrate1 = breathingrate1;
  }

  public Integer getBreathingrate2() {
    return breathingrate2;
  }

  public void setBreathingrate2(Integer breathingrate2) {
    this.breathingrate2 = breathingrate2;
  }

  public String getChick1id() {
    return chick1id;
  }

  public void setChick1id(String chick1id) {
    this.chick1id = chick1id;
  }

  public Integer getChick1NumberFeeds() {
    return chick1NumberFeeds;
  }

  public void setChick1NumberFeeds(Integer chick1NumberFeeds) {
    this.chick1NumberFeeds = chick1NumberFeeds;
  }

  public Integer getChick1TimeFed() {
    return chick1TimeFed;
  }

  public void setChick1TimeFed(Integer chick1TimeFed) {
    this.chick1TimeFed = chick1TimeFed;
  }

  public String getChick2id() {
    return chick2id;
  }

  public void setChick2id(String chick2id) {
    this.chick2id = chick2id;
  }

  public Integer getChick2NumberFeeds() {
    return chick2NumberFeeds;
  }

  public void setChick2NumberFeeds(Integer chick2NumberFeeds) {
    this.chick2NumberFeeds = chick2NumberFeeds;
  }

  public Integer getChick2TimeFed() {
    return chick2TimeFed;
  }

  public void setChick2TimeFed(Integer chick2TimeFed) {
    this.chick2TimeFed = chick2TimeFed;
  }

  public Date getChickfirstfeed1() {
    return chickfirstfeed1;
  }

  public void setChickfirstfeed1(Date chickfirstfeed1) {
    this.chickfirstfeed1 = chickfirstfeed1;
  }

  public Date getChickfirstfeed2() {
    return chickfirstfeed2;
  }

  public void setChickfirstfeed2(Date chickfirstfeed2) {
    this.chickfirstfeed2 = chickfirstfeed2;
  }

  public Date getChickfirstfeed3() {
    return chickfirstfeed3;
  }

  public void setChickfirstfeed3(Date chickfirstfeed3) {
    this.chickfirstfeed3 = chickfirstfeed3;
  }

  public Date getChickfirstfeed4() {
    return chickfirstfeed4;
  }

  public void setChickfirstfeed4(Date chickfirstfeed4) {
    this.chickfirstfeed4 = chickfirstfeed4;
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

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Date getFinishObservationTime() {
    return finishObservationTime;
  }

  public void setFinishObservationTime(Date finishObservationTime) {
    this.finishObservationTime = finishObservationTime;
  }

  public Date getFirstTimeOff() {
    return firstTimeOff;
  }

  public void setFirstTimeOff(Date firstTimeOff) {
    this.firstTimeOff = firstTimeOff;
  }

  public Date getHatchDate() {
    return hatchDate;
  }

  public void setHatchDate(Date hatchDate) {
    this.hatchDate = hatchDate;
  }

  public Boolean getHeatpad1() {
    return heatpad1;
  }

  public void setHeatpad1(Boolean heatpad1) {
    this.heatpad1 = heatpad1;
  }

  public Boolean getHeatpad2() {
    return heatpad2;
  }

  public void setHeatpad2(Boolean heatpad2) {
    this.heatpad2 = heatpad2;
  }

  public Boolean getHeatpad3() {
    return heatpad3;
  }

  public void setHeatpad3(Boolean heatpad3) {
    this.heatpad3 = heatpad3;
  }

  public Boolean getHeatpad4() {
    return heatpad4;
  }

  public void setHeatpad4(Boolean heatpad4) {
    this.heatpad4 = heatpad4;
  }

  public Integer getHeatPadTime() {
    return heatPadTime;
  }

  public void setHeatPadTime(Integer heatPadTime) {
    this.heatPadTime = heatPadTime;
  }

  public Date getLayDate() {
    return layDate;
  }

  public void setLayDate(Date layDate) {
    this.layDate = layDate;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public Integer getMaxTimeOff() {
    return maxTimeOff;
  }

  public void setMaxTimeOff(Integer maxTimeOff) {
    this.maxTimeOff = maxTimeOff;
  }

  public Date getMumback1() {
    return mumback1;
  }

  public void setMumback1(Date mumback1) {
    this.mumback1 = mumback1;
  }

  public Date getMumback2() {
    return mumback2;
  }

  public void setMumback2(Date mumback2) {
    this.mumback2 = mumback2;
  }

  public Date getMumback3() {
    return mumback3;
  }

  public void setMumback3(Date mumback3) {
    this.mumback3 = mumback3;
  }

  public Date getMumback4() {
    return mumback4;
  }

  public void setMumback4(Date mumback4) {
    this.mumback4 = mumback4;
  }

  public String getNestObsId() {
    return nestObsId;
  }

  public void setNestObsId(String nestObsId) {
    this.nestObsId = nestObsId;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Integer getNumberOfChicks() {
    return numberOfChicks;
  }

  public void setNumberOfChicks(Integer numberOfChicks) {
    this.numberOfChicks = numberOfChicks;
  }

  public Integer getNumberOfEggs() {
    return numberOfEggs;
  }

  public void setNumberOfEggs(Integer numberOfEggs) {
    this.numberOfEggs = numberOfEggs;
  }

  public Integer getNumberOfTimesOff() {
    return numberOfTimesOff;
  }

  public void setNumberOfTimesOff(Integer numberOfTimesOff) {
    this.numberOfTimesOff = numberOfTimesOff;
  }

  public String getOldBirdId() {
    return oldBirdId;
  }

  public void setOldBirdId(String oldBirdId) {
    this.oldBirdId = oldBirdId;
  }

  public String getOldLocationName() {
    return oldLocationName;
  }

  public void setOldLocationName(String oldLocationName) {
    this.oldLocationName = oldLocationName;
  }

  public Float getPercentageOfDayActive() {
    return percentageOfDayActive;
  }

  public void setPercentageOfDayActive(Float percentageOfDayActive) {
    this.percentageOfDayActive = percentageOfDayActive;
  }

  public Float getPercentageOfNightActive() {
    return percentageOfNightActive;
  }

  public void setPercentageOfNightActive(Float percentageOfNightActive) {
    this.percentageOfNightActive = percentageOfNightActive;
  }

  public Integer getRolls() {
    return rolls;
  }

  public void setRolls(Integer rolls) {
    this.rolls = rolls;
  }

  public Integer getScratches() {
    return scratches;
  }

  public void setScratches(Integer scratches) {
    this.scratches = scratches;
  }

  public Date getStartObservationTime() {
    return startObservationTime;
  }

  public void setStartObservationTime(Date startObservationTime) {
    this.startObservationTime = startObservationTime;
  }

  public Integer getTimeOnNestDuringDaylight() {
    return timeOnNestDuringDaylight;
  }

  public void setTimeOnNestDuringDaylight(Integer timeOnNestDuringDaylight) {
    this.timeOnNestDuringDaylight = timeOnNestDuringDaylight;
  }

  public Integer getTimeOnNestDuringNight() {
    return timeOnNestDuringNight;
  }

  public void setTimeOnNestDuringNight(Integer timeOnNestDuringNight) {
    this.timeOnNestDuringNight = timeOnNestDuringNight;
  }

  public Integer getTotalTimeOff() {
    return totalTimeOff;
  }

  public void setTotalTimeOff(Integer totalTimeOff) {
    this.totalTimeOff = totalTimeOff;
  }

  public Integer getUnknownChickNumberFeeds() {
    return unknownChickNumberFeeds;
  }

  public void setUnknownChickNumberFeeds(Integer unknownChickNumberFeeds) {
    this.unknownChickNumberFeeds = unknownChickNumberFeeds;
  }

  public Integer getUnknownChickTimeFed() {
    return unknownChickTimeFed;
  }

  public void setUnknownChickTimeFed(Integer unknownChickTimeFed) {
    this.unknownChickTimeFed = unknownChickTimeFed;
  }

  public String getWeighMethod() {
    return weighMethod;
  }

  public void setWeighMethod(String weighMethod) {
    this.weighMethod = weighMethod;
  }

  public List<ChickRecordReferenceEntity> getChickRecordReferenceList() {
    return chickRecordReferenceList;
  }

  public void setChickRecordReferenceList(
      List<ChickRecordReferenceEntity> chickRecordReferenceList) {
    this.chickRecordReferenceList = chickRecordReferenceList;
  }

  public List<EggRecordReferenceEntity> getEggRecordReferenceList() {
    return eggRecordReferenceList;
  }

  public void setEggRecordReferenceList(List<EggRecordReferenceEntity> eggRecordReferenceList) {
    this.eggRecordReferenceList = eggRecordReferenceList;
  }

  public List<MotherTripEntity> getMotherTripList() {
    return motherTripList;
  }

  public void setMotherTripList(List<MotherTripEntity> motherTripList) {
    this.motherTripList = motherTripList;
  }

  public MotherTripSummaryEntity getMotherTripSummary() {
    return motherTripSummary;
  }

  public void setMotherTripSummary(MotherTripSummaryEntity motherTripSummary) {
    this.motherTripSummary = motherTripSummary;
  }

  public NestChamberEntity getNestChamber() {
    return nestChamber;
  }

  public void setNestChamber(NestChamberEntity nestChamber) {
    this.nestChamber = nestChamber;
  }

  public List<NestChickEntity> getNestChickList() {
    return nestChickList;
  }

  public void setNestChickList(List<NestChickEntity> nestChickList) {
    this.nestChickList = nestChickList;
  }

  public List<NestEggEntity> getNestEggList() {
    return nestEggList;
  }

  public void setNestEggList(List<NestEggEntity> nestEggList) {
    this.nestEggList = nestEggList;
  }

  public ObservationTimesEntity getObservationTimes() {
    return observationTimes;
  }

  public void setObservationTimes(ObservationTimesEntity observationTimes) {
    this.observationTimes = observationTimes;
  }

  public List<ObserverEntity> getObserverList() {
    return observerList;
  }

  public void setObserverList(List<ObserverEntity> observerList) {
    this.observerList = observerList;
  }

}
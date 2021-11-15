package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class RecordEntity extends BaseEntity {

  @Field
  private String activity;
  @Field
  private String birdCert;
  @Field
  private String birdID;
  @Field
  private Integer booming;
  @Field
  private String comments;
  @Field
  private Date dateTime;
  @Field
  private Integer duration;
  @Field
  private Float easting;
  @Field
  private Date entryDateTime;
  @Field
  private Boolean errol;
  @Field
  private Date firstArriveTime;
  @Field
  private String island;
  @Field
  private Date lastDepartTime;
  @Field
  private String locationID;
  @Field
  private String mappingMethod;
  @Field
  private Integer nesting;
  @Field
  private Float northing;
  @Field
  private Integer numberOfVisits;
  @Field
  private String obsQuality;
  @Field
  private String oldBirdID;
  @Field
  private Integer oldBoomingIntensity;
  @Field
  private String oldHealthStatus;
  @Field
  private String oldLocationDescription;
  @Field
  private String oldLocationName;
  @Field
  private String oldObserver;
  @Field
  private String reason;
  @Field
  private String recordID;
  @Field
  private String recordType;
  @Field
  private String signalRcvdLocation;
  @Field
  private Boolean significantEvent;
  @Field
  private String subReason;
  @Field
  private Integer supFed;
  @Field
  private String transmitterId;
  @Field
  private BandsEntity bands;
  @Field
  private BatteryLifeEntity batteryLife;
  @Field
  private BloodSampleDetailEntity bloodSampleDetail;
  @Field
  private CaptureDetailEntity captureDetail;
  @Field
  private CheckmateEntity checkmate;
  @Field
  private ChickHealthEntity chickHealth;
  @Field
  private ChipsEntity chips;
  @Field
  private EggHealthEntity eggHealth;
  @Field
  private EggTimerEntity eggTimer;
  @Field
  private HandRaiseEntity handRaise;
  @Field
  private HarnessChangeEntity harnessChange;
  @Field
  private HealthCheckEntity healthCheck;
  @Field
  private HealthStatusEntity healthStatus;
  @Field
  private List<LocationBearingEntity> locationBearingList;
  @Field
  private MeasureDetailEntity measureDetail;
  @Field
  private List<MedicationEntity> medicationList;
  @Field
  private List<ObserverEntity> observerList;
  @Field
  private List<OtherSampleEntity> otherSampleList;
  @Field
  private SnarkDataEntity snarkData;
  @Field
  private StandardEntity standard;
  @Field
  private List<SpermSampleEntity> spermSampleList;
  @Field
  private SupplementaryFeedingEntity supplementaryFeeding;
  @Field
  private List<SwabSampleEntity> swabSampleList;
  @Field
  private TransferDetailEntity transferDetail;
  @Field
  private TransmitterChangeEntity transmitterChange;
  @Field
  private TxActivityEntity txActivity;
  @Field
  private WeightEntity weight;
  @Field
  private Integer magneticDeclination;
  @Field
  private Integer magneticDeclinationAsOfYear;

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public String getBirdCert() {
    return birdCert;
  }

  public void setBirdCert(String birdCert) {
    this.birdCert = birdCert;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public Integer getBooming() {
    return booming;
  }

  public void setBooming(Integer booming) {
    this.booming = booming;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Float getEasting() {
    return easting;
  }

  public void setEasting(Float easting) {
    this.easting = easting;
  }

  public Date getEntryDateTime() {
    return entryDateTime;
  }

  public void setEntryDateTime(Date entryDateTime) {
    this.entryDateTime = entryDateTime;
  }

  public Boolean getErrol() {
    return errol;
  }

  public void setErrol(Boolean errol) {
    this.errol = errol;
  }

  public Date getFirstArriveTime() {
    return firstArriveTime;
  }

  public void setFirstArriveTime(Date firstArriveTime) {
    this.firstArriveTime = firstArriveTime;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public Date getLastDepartTime() {
    return lastDepartTime;
  }

  public void setLastDepartTime(Date lastDepartTime) {
    this.lastDepartTime = lastDepartTime;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getMappingMethod() {
    return mappingMethod;
  }

  public void setMappingMethod(String mappingMethod) {
    this.mappingMethod = mappingMethod;
  }

  public Integer getNesting() {
    return nesting;
  }

  public void setNesting(Integer nesting) {
    this.nesting = nesting;
  }

  public Float getNorthing() {
    return northing;
  }

  public void setNorthing(Float northing) {
    this.northing = northing;
  }

  public Integer getNumberOfVisits() {
    return numberOfVisits;
  }

  public void setNumberOfVisits(Integer numberOfVisits) {
    this.numberOfVisits = numberOfVisits;
  }

  public String getObsQuality() {
    return obsQuality;
  }

  public void setObsQuality(String obsQuality) {
    this.obsQuality = obsQuality;
  }

  public String getOldBirdID() {
    return oldBirdID;
  }

  public void setOldBirdID(String oldBirdID) {
    this.oldBirdID = oldBirdID;
  }

  public Integer getOldBoomingIntensity() {
    return oldBoomingIntensity;
  }

  public void setOldBoomingIntensity(Integer oldBoomingIntensity) {
    this.oldBoomingIntensity = oldBoomingIntensity;
  }

  public String getOldHealthStatus() {
    return oldHealthStatus;
  }

  public void setOldHealthStatus(String oldHealthStatus) {
    this.oldHealthStatus = oldHealthStatus;
  }

  public String getOldLocationDescription() {
    return oldLocationDescription;
  }

  public void setOldLocationDescription(String oldLocationDescription) {
    this.oldLocationDescription = oldLocationDescription;
  }

  public String getOldLocationName() {
    return oldLocationName;
  }

  public void setOldLocationName(String oldLocationName) {
    this.oldLocationName = oldLocationName;
  }

  public String getOldObserver() {
    return oldObserver;
  }

  public void setOldObserver(String oldObserver) {
    this.oldObserver = oldObserver;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getRecordID() {
    return recordID;
  }

  public void setRecordID(String recordID) {
    this.recordID = recordID;
  }

  public String getRecordType() {
    return recordType;
  }

  public void setRecordType(String recordType) {
    this.recordType = recordType;
  }

  public String getSignalRcvdLocation() {
    return signalRcvdLocation;
  }

  public void setSignalRcvdLocation(String signalRcvdLocation) {
    this.signalRcvdLocation = signalRcvdLocation;
  }

  public Boolean getSignificantEvent() {
    return significantEvent;
  }

  public void setSignificantEvent(Boolean significantEvent) {
    this.significantEvent = significantEvent;
  }

  public String getSubReason() {
    return subReason;
  }

  public void setSubReason(String subReason) {
    this.subReason = subReason;
  }

  public Integer getSupFed() {
    return supFed;
  }

  public void setSupFed(Integer supFed) {
    this.supFed = supFed;
  }

  public String getTransmitterId() {
    return transmitterId;
  }

  public void setTransmitterId(String transmitterId) {
    this.transmitterId = transmitterId;
  }

  public BandsEntity getBands() {
    return bands;
  }

  public void setBands(BandsEntity bands) {
    this.bands = bands;
  }

  public BatteryLifeEntity getBatteryLife() {
    return batteryLife;
  }

  public void setBatteryLife(BatteryLifeEntity batteryLife) {
    this.batteryLife = batteryLife;
  }

  public BloodSampleDetailEntity getBloodSampleDetail() {
    return bloodSampleDetail;
  }

  public void setBloodSampleDetail(BloodSampleDetailEntity bloodSampleDetail) {
    this.bloodSampleDetail = bloodSampleDetail;
  }

  public CaptureDetailEntity getCaptureDetail() {
    return captureDetail;
  }

  public void setCaptureDetail(CaptureDetailEntity captureDetail) {
    this.captureDetail = captureDetail;
  }

  public CheckmateEntity getCheckmate() {
    return checkmate;
  }

  public void setCheckmate(CheckmateEntity checkmate) {
    this.checkmate = checkmate;
  }

  public ChickHealthEntity getChickHealth() {
    return chickHealth;
  }

  public void setChickHealth(ChickHealthEntity chickHealth) {
    this.chickHealth = chickHealth;
  }

  public ChipsEntity getChips() {
    return chips;
  }

  public void setChips(ChipsEntity chips) {
    this.chips = chips;
  }

  public EggHealthEntity getEggHealth() {
    return eggHealth;
  }

  public void setEggHealth(EggHealthEntity eggHealth) {
    this.eggHealth = eggHealth;
  }

  public EggTimerEntity getEggTimer() {
    return eggTimer;
  }

  public void setEggTimer(EggTimerEntity eggTimer) {
    this.eggTimer = eggTimer;
  }

  public HandRaiseEntity getHandRaise() {
    return handRaise;
  }

  public void setHandRaise(HandRaiseEntity handRaise) {
    this.handRaise = handRaise;
  }

  public HarnessChangeEntity getHarnessChange() {
    return harnessChange;
  }

  public void setHarnessChange(HarnessChangeEntity harnessChange) {
    this.harnessChange = harnessChange;
  }

  public HealthCheckEntity getHealthCheck() {
    return healthCheck;
  }

  public void setHealthCheck(HealthCheckEntity healthCheck) {
    this.healthCheck = healthCheck;
  }

  public HealthStatusEntity getHealthStatus() {
    return healthStatus;
  }

  public void setHealthStatus(HealthStatusEntity healthStatus) {
    this.healthStatus = healthStatus;
  }

  public List<LocationBearingEntity> getLocationBearingList() {
    return locationBearingList;
  }

  public void setLocationBearingList(List<LocationBearingEntity> locationBearingList) {
    this.locationBearingList = locationBearingList;
  }

  public MeasureDetailEntity getMeasureDetail() {
    return measureDetail;
  }

  public void setMeasureDetail(MeasureDetailEntity measureDetail) {
    this.measureDetail = measureDetail;
  }

  public List<MedicationEntity> getMedicationList() {
    return medicationList;
  }

  public void setMedicationList(List<MedicationEntity> medicationList) {
    this.medicationList = medicationList;
  }

  public List<ObserverEntity> getObserverList() {
    return observerList;
  }

  public void setObserverList(List<ObserverEntity> observerList) {
    this.observerList = observerList;
  }

  public List<OtherSampleEntity> getOtherSampleList() {
    return otherSampleList;
  }

  public void setOtherSampleList(List<OtherSampleEntity> otherSampleList) {
    this.otherSampleList = otherSampleList;
  }

  public SnarkDataEntity getSnarkData() {
    return snarkData;
  }

  public void setSnarkData(SnarkDataEntity snarkData) {
    this.snarkData = snarkData;
  }

  public StandardEntity getStandard() {
    return standard;
  }

  public void setStandard(StandardEntity standard) {
    this.standard = standard;
  }


  public List<SpermSampleEntity> getSpermSampleList() {
    return spermSampleList;
  }

  public void setSpermSampleList(List<SpermSampleEntity> spermSampleList) {
    this.spermSampleList = spermSampleList;
  }

  public SupplementaryFeedingEntity getSupplementaryFeeding() {
    return supplementaryFeeding;
  }

  public void setSupplementaryFeeding(SupplementaryFeedingEntity supplementaryFeeding) {
    this.supplementaryFeeding = supplementaryFeeding;
  }

  public List<SwabSampleEntity> getSwabSampleList() {
    return swabSampleList;
  }

  public void setSwabSampleList(List<SwabSampleEntity> swabSampleList) {
    this.swabSampleList = swabSampleList;
  }

  public TransferDetailEntity getTransferDetail() {
    return transferDetail;
  }

  public void setTransferDetail(TransferDetailEntity transferDetail) {
    this.transferDetail = transferDetail;
  }

  public TransmitterChangeEntity getTransmitterChange() {
    return transmitterChange;
  }

  public void setTransmitterChange(TransmitterChangeEntity transmitterChange) {
    this.transmitterChange = transmitterChange;
  }

  public TxActivityEntity getTxActivity() {
    return txActivity;
  }

  public void setTxActivity(TxActivityEntity txActivity) {
    this.txActivity = txActivity;
  }

  public WeightEntity getWeight() {
    return weight;
  }

  public void setWeight(WeightEntity weight) {
    this.weight = weight;
  }

  public Integer getMagneticDeclination() { return magneticDeclination; }

  public void setMagneticDeclination(Integer magneticDeclination) {
    this.magneticDeclination = magneticDeclination;
  }

  public Integer getMagneticDeclinationAsOfYear() { return magneticDeclinationAsOfYear; }

  public void setMagneticDeclinationAsOfYear(Integer magneticDeclinationAsOfYear) {
    this.magneticDeclinationAsOfYear = magneticDeclinationAsOfYear;
  }
}
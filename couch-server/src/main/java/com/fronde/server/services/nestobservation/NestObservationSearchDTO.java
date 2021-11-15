package com.fronde.server.services.nestobservation;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class NestObservationSearchDTO {

  private String id;
  private String locationType;
  private Date dateTime;
  private String birdID;
  private String birdName;
  private String island;
  private String locationID;
  private String locationName;
  private List<ChildBirdDTO> children = new LinkedList<>();

  public List<ObserverDTO> getObservers() {
    return observers;
  }

  public void setObservers(List<ObserverDTO> observers) {
    this.observers = observers;
  }

  private List<ObserverDTO> observers = new LinkedList<>();
  private String childNamesCSVList;

  public String getLocationType() {
    return locationType;
  }

  public void setLocationType(String locationType) {
    this.locationType = locationType;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDate(Date dateTime) {
    this.dateTime = dateTime;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public String getLocationID() {
    return locationID;
  }

  public void setLocationID(String locationID) {
    this.locationID = locationID;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public List<ChildBirdDTO> getChildren() {
    return children;
  }

  public void setChildren(List<ChildBirdDTO> children) {
    this.children = children;
  }

  public String getChildNamesCSVList() {
    return childNamesCSVList;
  }

  public void setChildNamesCSVList(String childNamesCSVList) {
    this.childNamesCSVList = childNamesCSVList;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}

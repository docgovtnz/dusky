package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class ObserverEntity {

  @Field
  private String personID;
  @Field
  private String observerCapacity;
  @Field
  private List<String> observationRoles;

  public String getPersonID() {
    return personID;
  }

  public void setPersonID(String personID) {
    this.personID = personID;
  }

  public String getObserverCapacity() {
    return observerCapacity;
  }

  public void setObserverCapacity(String observerCapacity) {
    this.observerCapacity = observerCapacity;
  }

  public List<String> getObservationRoles() {
    return observationRoles;
  }

  public void setObservationRoles(List<String> observationRoles) {
    this.observationRoles = observationRoles;
  }

}
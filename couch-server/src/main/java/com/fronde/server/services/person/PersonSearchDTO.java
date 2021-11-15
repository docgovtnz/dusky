package com.fronde.server.services.person;

import java.util.Date;

public class PersonSearchDTO {

  private Date accountExpiry;
  private String currentCapacity;
  private Boolean hasAccount;
  private String name;
  private String personRole;
  private String phoneNumber;
  private boolean expired;
  private String personID;

  public Date getAccountExpiry() {
    return accountExpiry;
  }

  public void setAccountExpiry(Date accountExpiry) {
    this.accountExpiry = accountExpiry;
  }

  public String getCurrentCapacity() {
    return currentCapacity;
  }

  public void setCurrentCapacity(String currentCapacity) {
    this.currentCapacity = currentCapacity;
  }

  public Boolean getHasAccount() {
    return hasAccount;
  }

  public void setHasAccount(Boolean hasAccount) {
    this.hasAccount = hasAccount;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPersonRole() {
    return personRole;
  }

  public void setPersonRole(String personRole) {
    this.personRole = personRole;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public boolean isExpired() {
    return expired;
  }

  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  public String getPersonID() {
    return personID;
  }

  public void setPersonID(String personID) {
    this.personID = personID;
  }
}

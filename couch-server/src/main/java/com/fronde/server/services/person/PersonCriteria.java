package com.fronde.server.services.person;

import com.fronde.server.domain.criteria.AbstractCriteria;

public class PersonCriteria extends AbstractCriteria {

  private String name;
  private Boolean activeOnly;
  private Boolean accountOnly;
  private String currentCapacity;
  private String personRole;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getActiveOnly() {
    return activeOnly;
  }

  public void setActiveOnly(Boolean activeOnly) {
    this.activeOnly = activeOnly;
  }

  public Boolean getAccountOnly() {
    return accountOnly;
  }

  public void setAccountOnly(Boolean accountOnly) {
    this.accountOnly = accountOnly;
  }

  public String getCurrentCapacity() {
    return currentCapacity;
  }

  public void setCurrentCapacity(String currentCapacity) {
    this.currentCapacity = currentCapacity;
  }

  public String getPersonRole() {
    return personRole;
  }

  public void setPersonRole(String personRole) {
    this.personRole = personRole;
  }

}

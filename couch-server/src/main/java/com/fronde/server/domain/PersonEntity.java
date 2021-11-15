package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class PersonEntity extends BaseEntity {

  @Field
  private Date accountExpiry;
  @Field
  private String comments;
  @Field
  private String currentCapacity;
  @Field
  private String emailAddress;
  @Field
  private Boolean hasAccount;
  @Field
  private String name;
  @Field
  private String personRole;
  @Field
  private String phoneNumber;
  @Field
  private String userName;

  public Date getAccountExpiry() {
    return accountExpiry;
  }

  public void setAccountExpiry(Date accountExpiry) {
    this.accountExpiry = accountExpiry;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getCurrentCapacity() {
    return currentCapacity;
  }

  public void setCurrentCapacity(String currentCapacity) {
    this.currentCapacity = currentCapacity;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

}
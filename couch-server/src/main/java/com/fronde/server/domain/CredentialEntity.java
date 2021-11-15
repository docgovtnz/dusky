package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;


@Document
public class CredentialEntity extends BaseEntity {

  @Field
  private Date accountExpiry;
  @Field
  private Date passwordExpiry;
  @Field
  private String passwordHash;
  @Field
  private String personId;

  public Date getAccountExpiry() {
    return accountExpiry;
  }

  public void setAccountExpiry(Date accountExpiry) {
    this.accountExpiry = accountExpiry;
  }

  public Date getPasswordExpiry() {
    return passwordExpiry;
  }

  public void setPasswordExpiry(Date passwordExpiry) {
    this.passwordExpiry = passwordExpiry;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }
}

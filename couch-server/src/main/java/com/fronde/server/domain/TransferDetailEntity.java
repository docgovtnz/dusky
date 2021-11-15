package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class TransferDetailEntity {

  @Field
  private String transferFromIsland;
  @Field
  private String transferToIsland;
  @Field
  private String transferFromLocationID;
  @Field
  private String transferToLocationID;
  @Field
  private String transferFromNestMother;
  @Field
  private String transferToNestMother;

  public String getTransferFromIsland() {
    return transferFromIsland;
  }

  public void setTransferFromIsland(String transferFromIsland) {
    this.transferFromIsland = transferFromIsland;
  }

  public String getTransferToIsland() {
    return transferToIsland;
  }

  public void setTransferToIsland(String transferToIsland) {
    this.transferToIsland = transferToIsland;
  }

  public String getTransferFromLocationID() {
    return transferFromLocationID;
  }

  public void setTransferFromLocationID(String transferFromLocationID) {
    this.transferFromLocationID = transferFromLocationID;
  }

  public String getTransferToLocationID() {
    return transferToLocationID;
  }

  public void setTransferToLocationID(String transferToLocationID) {
    this.transferToLocationID = transferToLocationID;
  }

  public String getTransferFromNestMother() {
    return transferFromNestMother;
  }

  public void setTransferFromNestMother(String transferFromNestMother) {
    this.transferFromNestMother = transferFromNestMother;
  }

  public String getTransferToNestMother() {
    return transferToNestMother;
  }

  public void setTransferToNestMother(String transferToNestMother) {
    this.transferToNestMother = transferToNestMother;
  }

}
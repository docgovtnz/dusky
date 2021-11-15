package com.fronde.server.services.transmitter;

import com.fronde.server.services.options.TransmitterSearchDTO;

public class TransmitterSearchPreDTO {

  private TransmitterSearchDTO searchDTO;
  private Boolean alive;
  private String birdID;
  private String birdName;

  public TransmitterSearchDTO getSearchDTO() {
    return searchDTO;
  }

  public void setSearchDTO(TransmitterSearchDTO searchDTO) {
    this.searchDTO = searchDTO;
  }

  public Boolean getAlive() {
    return alive;
  }

  public void setAlive(Boolean alive) {
    this.alive = alive;
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

  /**
   * Sets the details of the bird if the bird is alive and the transmitter is in one of the deployed
   * states.
   */
  public void populateBirdDetails() {
    boolean setDetails = false;
    if ((searchDTO.getStatus().equalsIgnoreCase("Deployed old")
        || searchDTO.getStatus().equalsIgnoreCase("Deployed new"))) {
      searchDTO.setBirdID(birdID);
      searchDTO.setBirdName(birdName);
    }
  }
}

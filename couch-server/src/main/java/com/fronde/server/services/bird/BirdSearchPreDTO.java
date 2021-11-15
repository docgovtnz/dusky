package com.fronde.server.services.bird;

import java.util.Date;

class BirdSearchPreDTO {

  private BirdSearchDTO searchDTO;
  private Date dateLaid;
  private Date dateHatched;
  private Date dateFirstFound;
  private Float estAgeWhen1stFound;
  private String viable;
  private Date demise;

  public BirdSearchDTO getSearchDTO() {
    return searchDTO;
  }

  public void setSearchDTO(BirdSearchDTO searchDTO) {
    this.searchDTO = searchDTO;
  }

  public Date getDateLaid() {
    return dateLaid;
  }

  public void setDateLaid(Date dateLaid) {
    this.dateLaid = dateLaid;
  }

  public Date getDateHatched() {
    return dateHatched;
  }

  public void setDateHatched(Date dateHatched) {
    this.dateHatched = dateHatched;
  }

  public Date getDateFirstFound() {
    return dateFirstFound;
  }

  public void setDateFirstFound(Date dateFirstFound) {
    this.dateFirstFound = dateFirstFound;
  }

  public Float getEstAgeWhen1stFound() {
    return estAgeWhen1stFound;
  }

  public void setEstAgeWhen1stFound(Float estAgeWhen1stFound) {
    this.estAgeWhen1stFound = estAgeWhen1stFound;
  }

  public String getViable() {
    return viable;
  }

  public void setViable(String viable) {
    this.viable = viable;
  }

  public Date getDemise() {
    return demise;
  }

  public void setDemise(Date demise) {
    this.demise = demise;
  }

  public void populateSearchDTOAgeInDays() {
    this.searchDTO.setAgeInDays(BirdAgeUtils
        .calculateAgeInDays(dateLaid, dateHatched, dateFirstFound, estAgeWhen1stFound,
            searchDTO.getAlive(), viable, demise));
  }

}

package com.fronde.server.services.snarkimport;

import java.util.List;

/**
 * Map selected records with evening date to be processed
 *
 */
public class SnarkProcessRequest {

  private List<EveningDTO> eveningList;

  public List<EveningDTO> getEveningList() {
    return eveningList;
  }

  public void setEveningList(List<EveningDTO> eveningList) {
    this.eveningList = eveningList;
  }

}

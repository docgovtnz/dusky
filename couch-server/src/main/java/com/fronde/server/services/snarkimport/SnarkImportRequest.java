package com.fronde.server.services.snarkimport;

import com.fronde.server.domain.SnarkImportEntity;
import java.util.List;

public class SnarkImportRequest {

  private SnarkImportEntity entity;
  private List<EveningDTO> eveningList;

  public SnarkImportRequest() {
  }

  public SnarkImportEntity getEntity() {
    return entity;
  }

  public void setEntity(SnarkImportEntity entity) {
    this.entity = entity;
  }

  public List<EveningDTO> getEveningList() {
    return eveningList;
  }

  public void setEveningList(List<EveningDTO> eveningList) {
    this.eveningList = eveningList;
  }
}

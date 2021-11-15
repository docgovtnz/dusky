package com.fronde.server.services.snarkimport;

import java.util.ArrayList;
import java.util.List;

public class SnarkCheckResultDTO {

  private List<MysteryWeightDTO> mysteryWeightList = new ArrayList<>();
  private List<EveningDTO> eveningList = new ArrayList<>();
  private boolean existingImport;
  private boolean someBirdsNotFound;
  private String island;
  private boolean showLockRecords;

  public List<EveningDTO> getEveningList() {
    return eveningList;
  }

  public void setEveningList(List<EveningDTO> eveningList) {
    this.eveningList = eveningList;
  }

  public List<MysteryWeightDTO> getMysteryWeightList() {
    return mysteryWeightList;
  }

  public void setMysteryWeightList(List<MysteryWeightDTO> mysteryWeightList) {
    this.mysteryWeightList = mysteryWeightList;
  }

  public boolean isExistingImport() {
    return existingImport;
  }

  public void setExistingImport(boolean existingImport) {
    this.existingImport = existingImport;
  }

  public boolean isSomeBirdsNotFound() {
    return someBirdsNotFound;
  }

  public void setSomeBirdsNotFound(boolean someBirdsNotFound) {
    this.someBirdsNotFound = someBirdsNotFound;
  }

  public String getIsland() {
    return island;
  }

  public void setIsland(String island) {
    this.island = island;
  }

  public boolean isShowLockRecords() { return showLockRecords; }

  public void setShowLockRecords(boolean showLockRecords) {
    this.showLockRecords = showLockRecords;
  }
}

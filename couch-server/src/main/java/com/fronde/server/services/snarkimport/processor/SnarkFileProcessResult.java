package com.fronde.server.services.snarkimport.processor;

import com.fronde.server.domain.SnarkRecordEntity;
import com.fronde.server.services.snarkimport.MysteryWeightDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnarkFileProcessResult {

  private List<MysteryWeightDTO> mysteryWeightList = new ArrayList<>();
  private Map<Date, List<SnarkRecordEntity>> snarkRecordsByEvening = new HashMap<>();
  private boolean showLockRecords;

  public void addSnarkRecord(Date evening, SnarkRecordEntity snarkRecord) {
    if (!snarkRecordsByEvening.containsKey(evening)) {
      snarkRecordsByEvening.put(evening, new ArrayList<>());
    }
    snarkRecordsByEvening.get(evening).add(snarkRecord);
  }

  public List<MysteryWeightDTO> getMysteryWeightList() {
    return mysteryWeightList;
  }

  public void setMysteryWeightList(List<MysteryWeightDTO> mysteryWeightList) {
    this.mysteryWeightList = mysteryWeightList;
  }

  public Map<Date, List<SnarkRecordEntity>> getSnarkRecordsByEvening() {
    return snarkRecordsByEvening;
  }

  public void setSnarkRecordsByEvening(Map<Date, List<SnarkRecordEntity>> snarkRecordsByEvening) {
    this.snarkRecordsByEvening = snarkRecordsByEvening;
  }

  public boolean isShowLockRecords() { return showLockRecords; }

  public void setShowLockRecords(boolean showLockRecords) {
    this.showLockRecords = showLockRecords;
  }
}

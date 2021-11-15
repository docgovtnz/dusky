package com.fronde.server.services.snarkimport;

import com.fronde.server.domain.SnarkRecordEntity;
import java.util.Date;
import java.util.List;

public class EveningDTO {

  private Date date;
  private List<SnarkRecordEntity> snarkRecordList;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public List<SnarkRecordEntity> getSnarkRecordList() {
    return snarkRecordList;
  }

  public void setSnarkRecordList(List<SnarkRecordEntity> snarkRecordList) {
    this.snarkRecordList = snarkRecordList;
  }

}

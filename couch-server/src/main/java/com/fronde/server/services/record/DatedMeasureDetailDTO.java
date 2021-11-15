package com.fronde.server.services.record;

import com.fronde.server.domain.MeasureDetailEntity;
import java.util.Date;

public class DatedMeasureDetailDTO {

  private Date dateTime;
  private MeasureDetailEntity measureDetail;

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public MeasureDetailEntity getMeasureDetail() {
    return measureDetail;
  }

  public void setMeasureDetail(MeasureDetailEntity measureDetail) {
    this.measureDetail = measureDetail;
  }

}

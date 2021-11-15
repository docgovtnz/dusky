package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import java.util.Date;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class ObservationTimesEntity {

  @Field
  private Date finishObservationTime;
  @Field
  private Date inspectionTime;

  public Date getFinishObservationTime() {
    return finishObservationTime;
  }

  public void setFinishObservationTime(Date finishObservationTime) {
    this.finishObservationTime = finishObservationTime;
  }

  public Date getInspectionTime() {
    return inspectionTime;
  }

  public void setInspectionTime(Date inspectionTime) {
    this.inspectionTime = inspectionTime;
  }

}
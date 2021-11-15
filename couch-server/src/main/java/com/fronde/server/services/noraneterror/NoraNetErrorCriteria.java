package com.fronde.server.services.noraneterror;

import com.fronde.server.domain.criteria.AbstractCriteria;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.util.Date;

/**
 * @version 1.0
 * @date 14/07/2021
 */

public class NoraNetErrorCriteria extends AbstractCriteria {

  private Integer islandNo;
  private String fileType;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date fromDateProcessed;
  // assume dates in all criteria objects are dates and not date times
  @DateTimeFormat(iso = ISO.DATE)
  private Date toDateProcessed;

  public Integer getIslandNo() {
    return islandNo;
  }

  public void setIslandNo(Integer islandNo) {
    this.islandNo = islandNo;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public Date getFromDateProcessed() {
    return fromDateProcessed;
  }

  public void setFromDateProcessed(Date fromDateProcessed) {
    this.fromDateProcessed = fromDateProcessed;
  }

  public Date getToDateProcessed() {
    return toDateProcessed;
  }

  public void setToDateProcessed(Date toDateProcessed) {
    this.toDateProcessed = toDateProcessed;
  }

}

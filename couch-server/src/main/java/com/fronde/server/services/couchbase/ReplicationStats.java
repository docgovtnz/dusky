package com.fronde.server.services.couchbase;

import java.util.Date;

public class ReplicationStats {

  private Date periodEnding;
  private int docsRead;
  private int docsWritten;
  private int docWriteFailures;

  public ReplicationStats() {
  }

  public Date getPeriodEnding() {
    return periodEnding;
  }

  public void setPeriodEnding(Date periodEnding) {
    this.periodEnding = periodEnding;
  }

  public int getDocsRead() {
    return docsRead;
  }

  public void setDocsRead(int docsRead) {
    this.docsRead = docsRead;
  }

  public int getDocsWritten() {
    return docsWritten;
  }

  public void setDocsWritten(int docsWritten) {
    this.docsWritten = docsWritten;
  }

  public int getDocWriteFailures() {
    return docWriteFailures;
  }

  public void setDocWriteFailures(int docWriteFailures) {
    this.docWriteFailures = docWriteFailures;
  }
}

package com.fronde.server.services.couchbase;

/**
 *
 */
public class Replication {

  private String type;
  private String replication_id;
  private Boolean continuous;
  private String source;
  private String target;
  private Integer docs_read;
  private Integer docs_written;
  private Integer doc_write_failures;
  private Integer start_last_seq;
  private Integer end_last_seq;


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getReplication_id() {
    return replication_id;
  }

  public void setReplication_id(String replication_id) {
    this.replication_id = replication_id;
  }

  public Boolean getContinuous() {
    return continuous;
  }

  public void setContinuous(Boolean continuous) {
    this.continuous = continuous;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public Integer getDocs_read() {
    return docs_read;
  }

  public void setDocs_read(Integer docs_read) {
    this.docs_read = docs_read;
  }

  public Integer getDocs_written() {
    return docs_written;
  }

  public void setDocs_written(Integer docs_written) {
    this.docs_written = docs_written;
  }

  public Integer getDoc_write_failures() {
    return doc_write_failures;
  }

  public void setDoc_write_failures(Integer doc_write_failures) {
    this.doc_write_failures = doc_write_failures;
  }

  public Integer getStart_last_seq() {
    return start_last_seq;
  }

  public void setStart_last_seq(Integer start_last_seq) {
    this.start_last_seq = start_last_seq;
  }

  public Integer getEnd_last_seq() {
    return end_last_seq;
  }

  public void setEnd_last_seq(Integer end_last_seq) {
    this.end_last_seq = end_last_seq;
  }
}

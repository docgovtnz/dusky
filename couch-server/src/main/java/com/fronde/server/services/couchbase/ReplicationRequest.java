package com.fronde.server.services.couchbase;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplicationRequest {

  private String replication_id;
  private String source;
  private String target;
  private Boolean continuous;
  private Boolean cancel;
  private Boolean async = Boolean.TRUE;
  private Integer changes_feed_limit = 200;

  private String filter;
  private String[] query_params;

  public ReplicationRequest() {
  }

  public ReplicationRequest(String replication_id, String source, String target) {
    this.replication_id = replication_id;
    this.source = source;
    this.target = target;
  }

  public String getReplication_id() {
    return replication_id;
  }

  public void setReplication_id(String replication_id) {
    this.replication_id = replication_id;
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

  public Boolean getContinuous() {
    return continuous;
  }

  public void setContinuous(Boolean continuous) {
    this.continuous = continuous;
  }

  public Boolean getCancel() {
    return cancel;
  }

  public void setCancel(Boolean cancel) {
    this.cancel = cancel;
  }

  public Boolean getAsync() {
    return async;
  }

  public void setAsync(Boolean async) {
    this.async = async;
  }

  public int getChanges_feed_limit() {
    return changes_feed_limit;
  }

  public void setChanges_feed_limit(int changes_feed_limit) {
    this.changes_feed_limit = changes_feed_limit;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String[] getQuery_params() {
    return query_params;
  }

  public void setQuery_params(String[] query_params) {
    this.query_params = query_params;
  }

  @Override
  public String toString() {
    return "ReplicationRequest{" +
        "replication_id='" + replication_id + '\'' +
        ", source='" + source + '\'' +
        ", target='" + target + '\'' +
        '}';
  }
}

package com.fronde.server.domain.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class PagedResponse<T> extends AbstractResponse<T> {

  protected int page;
  protected int pageSize;
  protected int total;
  protected List<T> results;

  public PagedResponse() {
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  @JsonProperty
  public int getFirstResult() {
    return (page - 1) * pageSize + 1;
  }

  @JsonProperty
  public int getLastResult() {
    return Math.min(page * pageSize, total);
  }


  public List<T> getResults() {
    if (results == null) {
      results = new ArrayList<T>();
    }
    return results;
  }

  public void setResults(List<T> results) {
    this.results = results;
  }
}

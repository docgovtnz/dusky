package com.fronde.server.domain.criteria;

/**
 *
 */
public abstract class AbstractCriteria implements PagedCriteria {

  private int pageNumber = 1;

  // This will get overridden by the web client code, but it's still useful to
  // set here, so that there's something returned for unit tests.
  private int pageSize = 10;

  public AbstractCriteria() {
  }

  @Override
  public int getPageNumber() {
    return pageNumber;
  }

  @Override
  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  @Override
  public int getPageSize() {
    return pageSize;
  }

  @Override
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  @Override
  public int getOffset() {
    return (pageNumber - 1) * pageSize;
  }

}

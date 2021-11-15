package com.fronde.server.domain.criteria;

/**
 *
 */
public interface PagedCriteria {

  int getPageNumber();

  void setPageNumber(int pageNumber);

  int getPageSize();

  void setPageSize(int pageSize);

  int getOffset();
}

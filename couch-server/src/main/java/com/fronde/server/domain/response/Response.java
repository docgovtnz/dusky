package com.fronde.server.domain.response;

/**
 *
 */
public class Response<T> extends AbstractResponse<T> {

  private T model;

  public Response() {
  }

  public Response(T model) {
    this.model = model;
  }

  public T getModel() {
    return model;
  }

  public void setModel(T model) {
    this.model = model;
  }
}

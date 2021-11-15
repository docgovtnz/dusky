package com.fronde.server.domain.response;

/**
 *
 */
public class DeleteByIdCheckDTO {

  private String id;
  private Boolean deleteOk;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Boolean getDeleteOk() {
    return deleteOk;
  }

  public void setDeleteOk(Boolean deleteOk) {
    this.deleteOk = deleteOk;
  }
}

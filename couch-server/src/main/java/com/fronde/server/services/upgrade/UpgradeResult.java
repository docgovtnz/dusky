package com.fronde.server.services.upgrade;

public class UpgradeResult {

  private String result;

  public UpgradeResult() {
  }

  public UpgradeResult(String result) {
    this.result = result;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Override
  public String toString() {
    return "UpgradeResult{" +
        "result='" + result + '\'' +
        '}';
  }
}

package com.fronde.server.services.feedout;

public class FoodTallySearchDTO {

  private String name;
  private Integer in;
  private Integer out;
  private Integer consumed;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getIn() {
    return in;
  }

  public void setIn(Integer in) {
    this.in = in;
  }

  public Integer getOut() {
    return out;
  }

  public void setOut(Integer out) {
    this.out = out;
  }

  public Integer getConsumed() {
    return consumed;
  }

  public void setConsumed(Integer consumed) {
    this.consumed = consumed;
  }

}

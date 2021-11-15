package com.fronde.server.services.weight;

import java.util.LinkedList;
import java.util.List;

public class LegendItem {

  private String label;
  private String birdID;
  private int type = 0;
  private boolean hidden = false;
  private int borderWidth = 1;
  private String borderColor;
  private boolean referenceData;
  private int[] borderDash = new int[]{};
  private String backgroundColor;
  private List<Dataset> datasets = new LinkedList<>();


  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }


  public List<Dataset> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<Dataset> datasets) {
    this.datasets = datasets;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public int getBorderWidth() {
    return borderWidth;
  }

  public void setBorderWidth(int borderWidth) {
    this.borderWidth = borderWidth;
  }

  public String getBorderColor() {
    return borderColor;
  }


  public void setBorderColor(String borderColor) {
    this.borderColor = borderColor;
  }

  public boolean isReferenceData() {
    return referenceData;
  }

  public void setReferenceData(boolean referenceData) {
    this.referenceData = referenceData;
  }

  public int[] getBorderDash() {
    return borderDash;
  }

  public void setBorderDash(int[] borderDash) {
    this.borderDash = borderDash;
  }

  public LegendItem withType(int type) {
    this.setType(type);
    return this;
  }

  public LegendItem withReferenceData(boolean referenceData) {
    this.setReferenceData(referenceData);
    return this;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public LegendItem withBackgroundColor(String backgroundColor) {
    this.setBackgroundColor(backgroundColor);
    return this;
  }

  public LegendItem withBorderColor(String borderColor) {
    this.setBorderColor(borderColor);
    return this;
  }
}

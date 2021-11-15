package com.fronde.server.services.weight;

import java.util.LinkedList;
import java.util.List;

public class Dataset {

  private String birdName;
  private String birdID;

  private String label;
  private List<Point> data = new LinkedList<>();
  private List<PointDate> dataDate = new LinkedList<>();
  private String borderColor;
  private int[] borderDash = new int[]{};
  private int pointRadius = 0;
  private double lineTension = 0.1;
  private boolean showLine = true;
  private String backgroundColor = "#FFFFFF";
  //private boolean fill = false;
  private int borderWidth = 1;
  private boolean hidden = false;
  private boolean referenceData = false;
  private boolean showOnLegend = true;
  private Object fill;

  public Dataset() {
  }

  public Dataset(Dataset source, boolean copyData) {
    this.birdName = source.birdName;
    this.birdID = source.birdID;
    this.label = source.label;
    this.borderColor = source.borderColor;
    this.borderDash = source.borderDash;
    this.pointRadius = source.pointRadius;
    this.lineTension = source.lineTension;
    this.showLine = source.showLine;
    this.backgroundColor = source.backgroundColor;
    this.fill = source.fill;
    this.borderWidth = source.borderWidth;
    this.hidden = source.hidden;
    this.referenceData = source.referenceData;
    this.showOnLegend = source.showOnLegend;
    if (copyData) {
      this.data = new LinkedList<>(source.data);
      this.dataDate = new LinkedList<>(source.dataDate);
    }
  }


  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public List<Point> getData() {
    return data;
  }

  public void setData(List<Point> data) {
    this.data = data;
  }

  public List<PointDate> getDataDate() {
    return dataDate;
  }

  public void setDataDate(List<PointDate> dataDate) {
    this.dataDate = dataDate;
  }

  public String getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(String borderColor) {
    this.borderColor = borderColor;
  }

  public int[] getBorderDash() {
    return borderDash;
  }

  public void setBorderDash(int[] borderDash) {
    this.borderDash = borderDash;
  }

  public int getPointRadius() {
    return pointRadius;
  }

  public void setPointRadius(int pointRadius) {
    this.pointRadius = pointRadius;
  }

  public double getLineTension() {
    return lineTension;
  }

  public void setLineTension(double lineTension) {
    this.lineTension = lineTension;
  }

  public String getBirdName() {
    return birdName;
  }

  public void setBirdName(String birdName) {
    this.birdName = birdName;
  }

  public String getBirdID() {
    return birdID;
  }

  public void setBirdID(String birdID) {
    this.birdID = birdID;
  }

  public boolean isShowLine() {
    return showLine;
  }

  public void setShowLine(boolean showLine) {
    this.showLine = showLine;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public Object getFill() {
    return fill;
  }

  public void setFill(Object fill) {
    this.fill = fill;
  }

  public int getBorderWidth() {
    return borderWidth;
  }

  public void setBorderWidth(int borderWidth) {
    this.borderWidth = borderWidth;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public boolean isReferenceData() {
    return referenceData;
  }

  public void setReferenceData(boolean referenceData) {
    this.referenceData = referenceData;
  }

  public boolean isShowOnLegend() {
    return showOnLegend;
  }

  public void setShowOnLegend(boolean showOnLegend) {
    this.showOnLegend = showOnLegend;
  }
}

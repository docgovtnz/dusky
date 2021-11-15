package com.fronde.server.services.weight;

import java.util.List;

public class EggReferenceDataDTO {

  private List<EggReferencePoint> referencePoints;

  public List<EggReferencePoint> getReferencePoints() {
    return referencePoints;
  }

  public void setReferencePoints(List<EggReferencePoint> referencePoints) {
    this.referencePoints = referencePoints;
  }
}

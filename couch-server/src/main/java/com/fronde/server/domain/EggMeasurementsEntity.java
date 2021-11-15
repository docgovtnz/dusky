package com.fronde.server.domain;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.couchbase.core.mapping.Document;

@Document
public class EggMeasurementsEntity {

  @Field
  private Float eggWeightBeforeFillingWithWater;
  @Field
  private Float germinalDiscDiameterInMms;
  @Field
  private Float shellWasteWeightHatched;
  @Field
  private Float dryShellWeightHatched;
  @Field
  private Float dryShellWeightUnhatched;
  @Field
  private Float weightWithWaterInGrams;
  @Field
  private Float shellThicknessInMms;
  @Field
  private Boolean addled;
  @Field
  private Float yolkWeightInGrams;
  @Field
  private Float albumenWeightInGrams;
  @Field
  private Float fwCoefficientX104;
  @Field
  private Float calculatedFreshWeight;
  @Field
  private Float eggLength;
  @Field
  private Float eggWidth;

  public Float getEggWeightBeforeFillingWithWater() {
    return eggWeightBeforeFillingWithWater;
  }

  public void setEggWeightBeforeFillingWithWater(Float eggWeightBeforeFillingWithWater) {
    this.eggWeightBeforeFillingWithWater = eggWeightBeforeFillingWithWater;
  }

  public Float getGerminalDiscDiameterInMms() {
    return germinalDiscDiameterInMms;
  }

  public void setGerminalDiscDiameterInMms(Float germinalDiscDiameterInMms) {
    this.germinalDiscDiameterInMms = germinalDiscDiameterInMms;
  }

  public Float getShellWasteWeightHatched() {
    return shellWasteWeightHatched;
  }

  public void setShellWasteWeightHatched(Float shellWasteWeightHatched) {
    this.shellWasteWeightHatched = shellWasteWeightHatched;
  }

  public Float getDryShellWeightHatched() {
    return dryShellWeightHatched;
  }

  public void setDryShellWeightHatched(Float dryShellWeightHatched) {
    this.dryShellWeightHatched = dryShellWeightHatched;
  }

  public Float getDryShellWeightUnhatched() {
    return dryShellWeightUnhatched;
  }

  public void setDryShellWeightUnhatched(Float dryShellWeightUnhatched) {
    this.dryShellWeightUnhatched = dryShellWeightUnhatched;
  }

  public Float getWeightWithWaterInGrams() {
    return weightWithWaterInGrams;
  }

  public void setWeightWithWaterInGrams(Float weightWithWaterInGrams) {
    this.weightWithWaterInGrams = weightWithWaterInGrams;
  }

  public Float getShellThicknessInMms() {
    return shellThicknessInMms;
  }

  public void setShellThicknessInMms(Float shellThicknessInMms) {
    this.shellThicknessInMms = shellThicknessInMms;
  }

  public Boolean getAddled() {
    return addled;
  }

  public void setAddled(Boolean addled) {
    this.addled = addled;
  }

  public Float getYolkWeightInGrams() { return yolkWeightInGrams; }

  public void setYolkWeightInGrams(Float yolkWeightInGrams) {
    this.yolkWeightInGrams = yolkWeightInGrams;
  }

  public Float getAlbumenWeightInGrams() { return albumenWeightInGrams; }

  public void setAlbumenWeightInGrams(Float albumenWeightInGrams) {
    this.albumenWeightInGrams = albumenWeightInGrams;
  }

  public Float getFwCoefficientX104() {
    return fwCoefficientX104;
  }

  public void setFwCoefficientX104(Float fwCoefficientX104) {
    this.fwCoefficientX104 = fwCoefficientX104;
  }

  public Float getCalculatedFreshWeight() {
    return calculatedFreshWeight;
  }

  public void setCalculatedFreshWeight(Float calculatedFreshWeight) {
    this.calculatedFreshWeight = calculatedFreshWeight;
  }

  public Float getEggLength() {
    return eggLength;
  }

  public void setEggLength(Float eggLength) {
    this.eggLength = eggLength;
  }

  public Float getEggWidth() {
    return eggWidth;
  }

  public void setEggWidth(Float eggWidth) {
    this.eggWidth = eggWidth;
  }


}
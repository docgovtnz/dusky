package com.fronde.server.services.snarkimport.processor;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnarkFileProcessorFactory {

  // set by special setter, see below
  private LocalTime eveningCutoff = LocalTime.of(12, 0);
  // set by special setter, see below
  private Duration absentThreshold = Duration.of(10, ChronoUnit.MINUTES);
  // set by special setter, see below
  private Duration presentThreshold = Duration.of(10, ChronoUnit.MINUTES);
  private Duration visitSelectionThreshold = Duration.of(5, ChronoUnit.MINUTES);
  @Value("${snarkimport.averageWeightSampleLimit:10}")
  private Integer averageWeightSampleLimit = 10;
  @Value("${snarkimport.weightToKgRatio:1000}")
  private Integer weightToKgRatio = 1000;
  @Value("${snarkimport.readerYearOffset:2000}")
  private Integer readerYearOffset = 2000;
  @Value("${snarkimport.weightAggregationMethod:AVERAGE}")
  private String weightAggregationMethod = SnarkFileProcessor.AGGREGATION_METHOD_AVERAGE;

  public LocalTime getEveningCutoff() {
    return eveningCutoff;
  }

  public void setEveningCutoff(LocalTime eveningCutoff) {
    this.eveningCutoff = eveningCutoff;
  }

  @Value("${snarkimport.eveningCutoff:12:00}")
  public void setEveningCutoff(String eveningCutoff) {
    this.eveningCutoff = LocalTime.parse(eveningCutoff, DateTimeFormatter.ISO_LOCAL_TIME);
  }

  public Duration getAbsentThreshold() {
    return absentThreshold;
  }

  public void setAbsentThreshold(Duration absentThreshold) {
    this.absentThreshold = absentThreshold;
  }

  @Value("${snarkimport.absentThresholdMinutes:10}")
  public void setAbsentThresholdMinutes(Integer absentThresholdMinutes) {
    this.absentThreshold = Duration.of(absentThresholdMinutes, ChronoUnit.MINUTES);
  }

  public Duration getPresentThreshold() {
    return presentThreshold;
  }

  public void setPresentThreshold(Duration presentThreshold) {
    this.presentThreshold = presentThreshold;
  }

  @Value("${snarkimport.presentThresholdMinutes:10}")
  public void setPresentThresholdMinutes(Integer presentThresholdMinutes) {
    this.presentThreshold = Duration.of(presentThresholdMinutes, ChronoUnit.MINUTES);
  }

  public Integer getAverageWeightSampleLimit() {
    return averageWeightSampleLimit;
  }

  public void setAverageWeightSampleLimit(Integer averageWeightSampleLimit) {
    this.averageWeightSampleLimit = averageWeightSampleLimit;
  }

  public Integer getWeightToKgRatio() {
    return weightToKgRatio;
  }

  public void setWeightToKgRatio(Integer weightToKgRatio) {
    this.weightToKgRatio = weightToKgRatio;
  }

  public Integer getReaderYearOffset() {
    return readerYearOffset;
  }

  public void setReaderYearOffset(Integer readerYearOffset) {
    this.readerYearOffset = readerYearOffset;
  }

  public Duration getVisitSelectionThreshold() {
    return visitSelectionThreshold;
  }

  public void setVisitSelectionThreshold(Duration visitSelectionThreshold) {
    this.visitSelectionThreshold = visitSelectionThreshold;
  }

  public String getWeightAggregationMethod() {
    return weightAggregationMethod;
  }

  public void setWeightAggregationMethod(String weightAggregationMethod) {
    this.weightAggregationMethod = weightAggregationMethod;
  }

  public SnarkFileProcessor createProcessor(BirdIdConverter converter) {
    return createProcessor(converter, null);
  }

  public SnarkFileProcessor createProcessor(BirdIdConverter converter, Integer qualityOverride) {
    SnarkFileProcessor processor = new SnarkFileProcessor();
    processor.setEveningCutoff(this.getEveningCutoff());
    processor.setAbsentThreshold(this.getAbsentThreshold());
    processor.setPresentThreshold(this.getPresentThreshold());
    processor.setAverageWeightSampleLimit(this.getAverageWeightSampleLimit());
    Optional.ofNullable(qualityOverride)
        .ifPresent(processor::setMinimumWeightQualityThreshold);
    processor.setWeightToKgRatio(this.getWeightToKgRatio());
    processor.setReaderYearOffset(this.getReaderYearOffset());
    processor.setWeightAggregationMethod(this.getWeightAggregationMethod());
    processor.setConverter(converter);
    processor.setVisitSelectionThreshold(this.getVisitSelectionThreshold());
    return processor;
  }

}

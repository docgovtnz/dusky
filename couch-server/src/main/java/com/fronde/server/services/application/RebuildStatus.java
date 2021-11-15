package com.fronde.server.services.application;


public class RebuildStatus {

  private String status;
  private int progress;
  private long startTime = -1;
  private String timeRemaining;

  public RebuildStatus() {

  }

  public RebuildStatus(String status, int progress) {
    this.status = status;
    this.progress = progress;
  }

  public void startTimer() {
    this.startTime = System.currentTimeMillis();
  }

  public String getTimeRemaining() {
    return this.timeRemaining;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getProgress() {
    if (this.startTime == -1) {
      this.startTimer();
    }
    if (progress < 15) {
      this.timeRemaining = "Calculating remaining time...";
    } else {

      float timeSinceStart = System.currentTimeMillis() - this.startTime;
      float minutesSinceStart = timeSinceStart / 60 / 1000;
      float minutesRemaining = minutesSinceStart / (progress / (float) 100) - minutesSinceStart;
      if (minutesRemaining <= 1) {
        this.timeRemaining = "About a minute remaining";
      } else {
        double remaining = Math.ceil(minutesRemaining);
        this.timeRemaining = String.format("Estimated %.0f minutes remaining", remaining);
      }
    }

    return progress;

  }

  public void setProgress(int progress) {
    this.progress = progress;
  }
}

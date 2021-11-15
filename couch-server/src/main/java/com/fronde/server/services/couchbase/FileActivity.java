package com.fronde.server.services.couchbase;

import java.util.List;

public class FileActivity {

  private int maxSize;
  private List<Long> fileActivity;

  public FileActivity() {
  }

  public int getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  public List<Long> getFileActivity() {
    return fileActivity;
  }

  public void setFileActivity(List<Long> fileActivity) {
    this.fileActivity = fileActivity;
  }
}

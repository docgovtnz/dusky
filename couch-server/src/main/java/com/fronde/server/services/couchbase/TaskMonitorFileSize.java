package com.fronde.server.services.couchbase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TaskMonitorFileSize {

  private static final Integer MAX_SIZE = 300;

  @Value("${sync.gateway.logFile}")
  protected String logFile;

  private final List<Long> fileActivity = new ArrayList<>();

  private Long lastLength;

  public FileActivity getFileActivity() {
    FileActivity activity = new FileActivity();
    activity.setMaxSize(MAX_SIZE);
    activity.setFileActivity(fileActivity);
    return activity;
  }

  public long monitorFileSize() {

    File file = new File(logFile);
    long currentLength = file.length();

    if (lastLength == null) {
      lastLength = currentLength;
    }

    long deltaLength = currentLength - lastLength;
    lastLength = currentLength;

    fileActivity.add(deltaLength);
    if (fileActivity.size() > MAX_SIZE) {
      fileActivity.remove(0);
    }

    return deltaLength;
  }
}

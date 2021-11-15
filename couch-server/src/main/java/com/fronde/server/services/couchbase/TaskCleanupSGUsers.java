package com.fronde.server.services.couchbase;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskCleanupSGUsers {

  private Date lastCheckedTime;

  @Autowired
  protected SGUserService sgUserService;

  public void checkUserCleanup() {

    Date checkTime = sgUserService.getStartOfToday();

    // if no check done yet, or Date has rolled to a new date
    if (lastCheckedTime == null || checkTime.after(lastCheckedTime)) {

      // do the cleanup, which might fail if SG is not running
      sgUserService.cleanup();

      // if cleanup fails then exception will be thrown and this next line will be skipped
      lastCheckedTime = checkTime;
    }
    // ignore and check again later
  }
}

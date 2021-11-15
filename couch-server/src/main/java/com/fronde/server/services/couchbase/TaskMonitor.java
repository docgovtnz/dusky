package com.fronde.server.services.couchbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Configuration
public class TaskMonitor implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger logger = LoggerFactory.getLogger(TaskMonitor.class);

  @Autowired
  protected ReplicationService replicationService;

  @Autowired
  protected TaskMonitorActiveTasks taskMonitorActiveTasks;

  @Autowired
  protected TaskMonitorFileSize taskMonitorFileSize;

  @Autowired
  protected TaskAutoConnect taskAutoConnect;

  @Autowired
  protected TaskCleanupSGUsers taskCleanupSGUsers;

  private Thread taskMonitorThread;

  private String syncGatewayStatus = "Unknown";

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (taskMonitorThread == null) {
      taskMonitorThread = new Thread("SyncGateway:TaskMonitor") {
        @Override
        public void run() {
          try {
            // start by first cancelling all pending tasks after a restart
            // we wait until this is successful before moving on to monitoring tasks forever
            TaskMonitor.this.cancelAllAfterStart();
            TaskMonitor.this.monitorTasksForever();
          } catch (Exception ex) {
            logger.error("Task monitor exception during startup.", ex);
          }
        }
      };

      taskMonitorThread.setDaemon(true);
      taskMonitorThread.start();
    }
  }

  public void doSleep(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException ex) {
    }
  }

  public void cancelAllAfterStart() {
    syncGatewayStatus = "Initialising";
    // TODO consider whether to perform several attempts first
    boolean allTasksCancelled = false;
    while (!allTasksCancelled) {
      try {
        taskMonitorActiveTasks.cancelAllTasks();
        // TODO consider whether to hold up replication service activities until we successfully pass this point
        allTasksCancelled = true;
      } catch (Exception ex) {
        logger.warn("Cancel of all tasks after start failed due to '" + ex + "'");
        doSleep(60000);
      }
    }
    replicationService.setCancelledAllAfterStart(true);
  }

  public void monitorTasksForever() {
    while (true) {
      try {
        taskCleanupSGUsers.checkUserCleanup();
        taskAutoConnect.checkAutoConnect();
        monitorTasksNow();
        syncGatewayStatus = "Ok";
        doSleep(10000);
      } catch (Exception ex) {
        syncGatewayStatus = "Error";
        logger.warn("Monitor of tasks failed due to '" + ex + "'");
        doSleep(60000);
      }
    }
  }

  /**
   * This has been pulled out of the while loop above so that it can be called immediately after a
   * connection request so that the new connection appears in the results.
   */
  public void monitorTasksNow() {
    taskMonitorActiveTasks.monitorActiveTasks();
    taskMonitorFileSize.monitorFileSize();
  }

  public SyncGatewayStatus getSyncGatewayStatus() {
    String serverMode = "UNKNOWN";
    LocalAddress localAddress = replicationService.getLocalAddress();
    if (localAddress != null) {
      serverMode = localAddress.getServerMode();
    }

    SyncGatewayStatus response = new SyncGatewayStatus();
    response.setStatus(syncGatewayStatus);
    response.setServerMode(serverMode);
    response.setConnectionMap(taskMonitorActiveTasks.getConnectionMap());
    response.setFileActivity(taskMonitorFileSize.getFileActivity());
    return response;
  }

  public void cancelAllTasks() {
    taskMonitorActiveTasks.cancelAllTasks();
  }

  public void disconnect(ConnectionRequest connectionRequest) {
    taskMonitorActiveTasks.disconnect(connectionRequest);
  }

  public void setAsMaster() {
    taskMonitorActiveTasks.setAsMaster();
  }

}

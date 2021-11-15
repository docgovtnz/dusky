package com.fronde.server.services.couchbase;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.validation.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TaskAutoConnect {

  private static final Logger logger = LoggerFactory.getLogger(TaskAutoConnect.class);


  @Value("${application.auto-connect.periodInSecs}")
  protected Integer autoConnectPeriodInSecs;

  @Autowired
  protected ReplicationService replicationService;

  @Autowired
  protected SyncGatewayService syncGatewayService;


  private Long nextCheckTime = 0L;

  public void onLoginCheckDataReplication() {
    try {
      replicationService.getLocalAuthRequest();
      checkAutoConnect(true);
    } catch (Exception ex) {
      // swallow
      logger.error("TaskAutoConnect exception during login", ex);
    }
  }

  public void checkAutoConnect() {
    replicationService.getLocalAuthRequest();
    checkAutoConnect(false);
  }

  public void checkAutoConnect(boolean immediate) {
    // Is the mode Client mode?
    LocalAddress localAddress = replicationService.getLocalAddress();
    String serverMode = localAddress.getServerMode();
    boolean isClientMode = serverMode != null && serverMode.equals("Client");
    if (!isClientMode) {
      // Return early
      return;
    }

    // Has the next check time passed?
    Long currentTime = System.currentTimeMillis();
    if (currentTime > nextCheckTime || immediate) {
      nextCheckTime = currentTime + (autoConnectPeriodInSecs * 1000);

      // Are we already connected?
      boolean alreadyConnected = replicationService.isAutoConnectAlreadyConnected();

      // If not already connected then attempt to connect to autoConnectAddress
      // catch and ignore failures, try again next period
      if (!alreadyConnected) {

        Response<ConnectionRequest> connectionRequestResponse = replicationService
            .connectToAutoConnectNode();

        if (connectionRequestResponse.getMessages().size() > 0) {
          logger
              .info("Auto connect failed. This is not a problem if not connected to the Internet.");
          for (ValidationMessage msg : connectionRequestResponse.getMessages()) {
            logger.info("Auto connect failed: " + msg.toString());
          }
        }
      } else {
        // If "connected" then make sure we can ping the target server. Once a connection has been created the SG
        // doesn't kill "active" tasks even if it has lost network connectivity. However in order to provide
        // better feedback to the user about the current network status and also because we won't want old releases
        // to replicate with new releases we are going to test connectivity now and if there is no connectivity
        // we will kill the active tasks.

        boolean reachable = replicationService.pingAutoConnectHost();
        if (!reachable) {
          replicationService.cancelAllTasks();
        }
      }
    }
  }

  public void resetNextCheckTime() {
    nextCheckTime = 0L;
  }
}

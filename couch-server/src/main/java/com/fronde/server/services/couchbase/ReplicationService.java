package com.fronde.server.services.couchbase;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.person.PersonCriteria;
import com.fronde.server.services.person.PersonRepository;
import com.fronde.server.services.validation.ValidationMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ReplicationService {

  private static final Logger logger = LoggerFactory.getLogger(ReplicationService.class);

  private Long lastSwitchedFromClient;
  private boolean cancelledAllAfterStart = false;

  @Value("${application.mode}")
  protected String serverMode;

  @Value("${server.port}")
  protected int serverPort;

  @Value("${replication.password:InvalidDefaultPassword}")
  protected String replicationPassword;

  @Value("${replication.installId:0}")
  protected Long replicationInstallId;

  @Value("${application.auto-connect.host}")
  protected String autoConnectHost;

  @Value("${application.auto-connect.port}")
  protected Integer autoConnectPort;

  @Value("${application.auto-connect.cancelWaitMillis:15000}")
  private Long autoConnectCancelWaitMillis;

  @Autowired
  protected TaskMonitor taskMonitor;

  @Autowired
  protected SyncGatewayClient syncGatewayClient;

  @Autowired
  protected SyncGatewayService syncGatewayService;

  @Autowired
  protected SGUserService sgUserService;

  @Autowired
  protected HostnameService hostnameService;

  private SGAuthenticationRequest autoConnectAuthRequest;

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  protected TaskAutoConnect taskAutoConnect;

  public LocalAddress getLocalAddress() {
    LocalAddress localAddress = new LocalAddress();
    localAddress.setServerMode(serverMode);
    localAddress.setAddress(hostnameToAddress(syncGatewayService.getExportedHostname()));
    return localAddress;
  }

  private String hostnameToAddress(String hostname) {
    return hostname + ":" + serverPort;
  }

  public Response<ConnectionRequest> connectToNode(ConnectionRequest connectionRequest) {
    return this.connectToNode(connectionRequest, false);
  }

  public Response<ConnectionRequest> connectToNode(ConnectionRequest connectionRequest,
      boolean autoConnect) {
    return this.connectToNode(connectionRequest, getLocalAuthRequest(), autoConnect);
  }

  public Response<ConnectionRequest> connectToNode(ConnectionRequest connectionRequest,
      SGAuthenticationRequest authenticationRequest, boolean autoConnect) {

    Response<ConnectionRequest> response = new Response<>(connectionRequest);
    try {
      if (!cancelledAllAfterStart) {
        throw new RuntimeException("Replication service is not yet ready - try again later");
      }

      if (!autoConnect && serverMode.equals("Client")) {
        throw new RuntimeException(
            "Cannot trigger a manual Client to Server connection - leave that to auto connect");
      }

      if (!getAllowedTargetAddresses().contains(connectionRequest.getConnectToServer())) {
        throw new RuntimeException(
            "Target " + connectionRequest.getConnectToServer() + " is not a permitted target");
      }

      if (serverMode.equals("Server") || serverMode.equals("Master")) {
        throw new RuntimeException("Operational mode of " + serverMode
            + " means this node can't create any connections, it can only receive them");
      }

      String targetServerMode = getTargetServerMode(connectionRequest);
      if (serverMode.equals("Client") && !targetServerMode.equals("Server")) {
        throw new RuntimeException(
            "If current mode is Client then the target machine must be in Server mode and cannot be in "
                + targetServerMode + " mode");
      }

      if (serverMode.equals("Peer") && !targetServerMode.equals("Master")) {
        throw new RuntimeException(
            "If current mode is Peer then the target machine must be in Master mode and cannot be in "
                + targetServerMode + " mode");
      }

      String targetServerAddress = connectionRequest.getConnectToServer();

      SGAuthenticationUser targetUser;
      SyncGatewayDetails target = syncGatewayClient.getSyncGatewayDetails(targetServerAddress);

      logger.info("Require authentication = " + target.isRequireAuthentication());
      if (target.isRequireAuthentication()) {
        SGAuthenticationResponse authenticationResponse = syncGatewayClient
            .authenticate(targetServerAddress, authenticationRequest);
        target = authenticationResponse.getSyncGatewayDetails();
        targetUser = authenticationResponse.getSgAuthenticationUser();
      } else {
        // Eventually, else should throw an exception
        targetUser = null;
      }

      SyncGatewayDetails source;
      SGAuthenticationUser sourceUser;
      if (authenticationRequest != null) {
        source = syncGatewayService.getExportedSyncGatewayDetails();

        // We create the local SG user with the same credentials from the targetUser, the username and password
        // for data replication needs to be the same locally as it is for the remote end of the replication
        sourceUser = sgUserService.createSGUser(targetUser);
      } else {
        // Eventually, else should throw an exception
        source = syncGatewayService.getExportedSyncGatewayDetails();
        sourceUser = null;
      }

      checkSyncGatewayConnectionRules(source, target);

      logger.info("Creating bi-directional replication: " + source + " with " + target);
      //String pushId = "Push::" + authenticationRequest.getUsername() + ":"  + target.getReplicationUrl(null);
      //String pullId = "Pull::" + authenticationRequest.getUsername() + ":"  + target.getReplicationUrl(null);
      String pushId = createReplicationId("Push", authenticationRequest.getUsername(),
          target.getReplicationUrl(null));
      String pullId = createReplicationId("Pull", authenticationRequest.getUsername(),
          target.getReplicationUrl(null));

      // if in Peer then wait until audo connect has had time to run it's course and cancel any Client to Server connections
      if ("Peer".equals(this.serverMode)) {
        if (this.lastSwitchedFromClient != null) {
          long elapsedSinceCancel = System.currentTimeMillis() - this.lastSwitchedFromClient;
          long waitTime = Math.max(0L, this.autoConnectCancelWaitMillis - elapsedSinceCancel);
          logger.info(
              "Switched from Client mode " + elapsedSinceCancel + "ms ago. Waiting " + waitTime
                  + "ms for auto connect cycle before cancelling tasks again");
          Thread.sleep(waitTime);
          // cancel all tasks in the hope of cancelling tasks created by auto connect
          logger.info(
              "Cancelling all tasks to cancel any tasks created by auto connect since mode was changed");
          cancelAllTasks();
        } else {
          logger.warn(
              "In Peer mode without having ever switched from Client mode. This is not expected");
        }
      }

      syncGatewayService.createReplication(pushId, source, sourceUser, target, targetUser);
      syncGatewayService.createReplication(pullId, target, targetUser, source, sourceUser);

      // needs a quick sleep to give couchbase a chance to work before monitoring it
      taskMonitor.doSleep(500);

      // Give the task monitor a quick poke now, so that the client can call and get the freshest data after this method returns.
      taskMonitor.monitorTasksNow();
    } catch (Exception ex) {
      ValidationMessage msg = new ValidationMessage();
      msg.setMessageText("Unable to connect: " + ex.getClass().getName() + ": " + ex.getMessage());
      if (ex.getCause() != null) {
        msg.setMessageText(
            msg.getMessageText() + " Cause: " + ex.getCause().getClass().getName() + ": " + ex
                .getCause().getMessage());
      }
      response.getMessages().add(msg);
    }

    return response;
  }

  private String createReplicationId(String direction, String userCredentials, String targetUrl) {
    try {
      if (replicationInstallId == null || replicationInstallId <= 0) {
        replicationInstallId = Math.abs(new Random().nextLong());
        FileUtils.writeLines(new File("replication.properties"),
            Collections.singletonList("\nreplication.installId=" + replicationInstallId), true);
      }

      if (replicationInstallId != null && replicationInstallId > 0) {
        return direction + "::" + replicationInstallId + ":" + userCredentials + ":" + targetUrl;
      } else {
        throw new RuntimeException(
            "No replicationInstallId is available. Check the replication.properties file.");
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void checkSyncGatewayConnectionRules(ConnectionRequest connectionRequest) {
    SyncGatewayDetails source = syncGatewayService.getExportedSyncGatewayDetails();
    SyncGatewayDetails target = syncGatewayClient
        .getSyncGatewayDetails(connectionRequest.getConnectToServer());
    checkSyncGatewayConnectionRules(source, target);
  }

  private void checkSyncGatewayConnectionRules(SyncGatewayDetails source,
      SyncGatewayDetails target) {
    if (!source.getVersion().equals(target.getVersion())) {
      throw new RuntimeException(
          "Application version number of this machine does not match the target machines version number. Version numbers are ("
              + source.getVersion() + ", " + target.getVersion() + ") (source, target).");
    }

    if (!source.getEnvironment().equals(target.getEnvironment())) {
      throw new RuntimeException(
          "Environment name of this machine does not match the target machines environment name. Environment names are ("
              + source.getEnvironment() + ", " + target.getEnvironment() + ") (source, target).");
    }
  }

  private String getTargetServerMode(ConnectionRequest connectionRequest) {
    String url =
        "https://" + connectionRequest.getConnectToServer() + "/api/replication/localAddress";
    RestTemplate restTemplate = new RestTemplate();
    LocalAddress targetLocalAddress = restTemplate.getForObject(url, LocalAddress.class);
    return targetLocalAddress.getServerMode();
  }

  public Response setMode(String mode) {
    if ("Server".equals(this.serverMode)) {
      throw new RuntimeException("Cannot change mode from Server - forbidden");
    }
    if ("Server".equals(mode)) {
      throw new RuntimeException("Cannot change mode to Server - forbidden");
    }

    // we track whether we are switching from client mode so we know how long to wait before
    // we can be satisfied auto connect has not triggered off another connection
    boolean switchedFromClient = "Client".equals(this.serverMode) && !"Client".equals(mode);

    boolean switchedToClient = !"Client".equals(this.serverMode) && "Client".equals(mode);

    if (switchedFromClient) {
      // cancel all tasks in the hope of cancelling tasks created by auto connect
      logger.info(
          "Cancelling all tasks in hope of cancelling any active tasks created by auto connect");
      cancelAllTasks();
      lastSwitchedFromClient = System.currentTimeMillis();
    }

    // If Client then connect to AWS Server
    // If Peer or Master disconnect any existing connections
    // If Server throw error

    if (mode.equals("Master")) {
      taskMonitor.setAsMaster();
    }

    if (switchedToClient) {
      logger.info("Cancelling all tasks to cancel any Peer to Master connections");
      cancelAllTasks();
      taskAutoConnect.resetNextCheckTime();
    }

    // only set the mode after any tasks above completed successfully
    this.serverMode = mode;

    Response response = new Response();
    return response;
  }

  public void cancelAllTasks() {
    taskMonitor.cancelAllTasks();
  }

  public void disconnect(ConnectionRequest connectionRequest) {
    taskMonitor.disconnect(connectionRequest);
  }

  public SyncGatewayStatus getSyncGatewayStatus() {
    return taskMonitor.getSyncGatewayStatus();
  }

  public boolean isAutoConnectAlreadyConnected() {
    boolean alreadyConnected = false;

    // Are there any active tasks?
    Replication[] activeTasks = syncGatewayService.getActiveTasks();
    if (activeTasks != null && activeTasks.length > 0) {
      for (Replication nextReplicationTask : activeTasks) {
        String target = nextReplicationTask.getTarget();
        if (target != null && target.toLowerCase().contains(autoConnectHost.toLowerCase())) {
          alreadyConnected = true;
          break;
        }
      }
    }

    return alreadyConnected;
  }

  public boolean pingAutoConnectHost() {
    try {
      checkSyncGatewayConnectionRules(getAutoConnectConnectionRequest());
      return true;
    } catch (Exception ex) {
      // exception is swallowed, but if there's code level bugs you might want some temporary logging here
      return false;
    }
  }

  private ConnectionRequest getAutoConnectConnectionRequest() {
    ConnectionRequest connectionRequest = new ConnectionRequest();
    connectionRequest.setConnectToServer(autoConnectHost + ":" + autoConnectPort);
    return connectionRequest;
  }

  public Response<ConnectionRequest> connectToAutoConnectNode() {
    return connectToNode(getAutoConnectConnectionRequest(), true);
  }

  public SGAuthenticationRequest getLocalAuthRequest() {
    // Has the replication password been set?
    if (replicationPassword == null || replicationPassword.equals("InvalidDefaultPassword")) {
      if (!serverMode.equals("Server")) {
        logger.error(
            "Replication password has not been set in replication.properties file. Data replication connections will not be created from this service.");
      }
    }

    if (autoConnectAuthRequest == null) {

      String username = hostnameService.getHostname();
      String password = this.replicationPassword;

      if (username != null && password != null) {
        autoConnectAuthRequest = new SGAuthenticationRequest(username, password);
      } else {
        logger.warn(
            "Unable to initialise data replication authentication - insufficient credentials");
      }
    }
    return autoConnectAuthRequest;
  }

  public List<String> getAllowedTargetAddresses() {
    PersonCriteria pc = new PersonCriteria();
    pc.setPageSize(Integer.MAX_VALUE);
    // TODO move to a constant
    pc.setPersonRole("Data Replication");
    List<PersonEntity> dataRepPeople = personRepository.findByCriteria(pc).getResults();
    List<String> allowedAddresses = new ArrayList<>();
    for (PersonEntity dataRepPerson : dataRepPeople) {
      allowedAddresses.add(hostnameToAddress(dataRepPerson.getUserName()));
    }
    // added the auto connect addresss
    allowedAddresses.add(autoConnectHost + ":" + autoConnectPort);
    // remove the local address
    allowedAddresses.remove(getLocalAddress().getAddress());
    return allowedAddresses;
  }

  public void setCancelledAllAfterStart(boolean cancelled) {
    this.cancelledAllAfterStart = cancelled;
  }
}

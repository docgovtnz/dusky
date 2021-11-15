package com.fronde.server.services.couchbase;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class TaskMonitorActiveTasks {

  private static final Logger logger = LoggerFactory.getLogger(TaskMonitorActiveTasks.class);

  @Autowired
  protected SyncGatewayService syncGatewayService;

  private final Map<String, Replication> lastSeenReplicationMap = new HashMap<>();
  private final Map<String, ReplicationConnection> connectionMap = new HashMap<>();


  public Map<String, ReplicationConnection> getConnectionMap() {
    return connectionMap;
  }


  public void monitorActiveTasks() {
    Replication[] activeTasks = syncGatewayService.getActiveTasks();

    for (Replication thisReplicationTask : activeTasks) {

      // "last" doesn't mean what was last in the array, it means what did the task look like the last time
      // we downloaded this particular replication id.
      Replication lastReplicationTask = lastSeenReplicationMap
          .get(thisReplicationTask.getReplication_id());
      if (lastReplicationTask == null) {
        // if we haven't seen this replicationId yet, then just use the current one, which means the delta
        // should be zero, but that's what we want for the first time around
        lastReplicationTask = thisReplicationTask;
      }

      // keep the thisReplicationTask for the next polling period to come
      lastSeenReplicationMap.put(thisReplicationTask.getReplication_id(), thisReplicationTask);

      // at this point we've got lastReplicationTask, and thisReplicationTask so calculate the differences
      int docsRead = thisReplicationTask.getDocs_read() - lastReplicationTask.getDocs_read();
      int docsWritten =
          thisReplicationTask.getDocs_written() - lastReplicationTask.getDocs_written();
      int docWriteFailures =
          thisReplicationTask.getDoc_write_failures() - lastReplicationTask.getDoc_write_failures();

      // convert the source and target into a consistent key that is based on the "local" sync gateway
      ConnectionKey connectionKey = toConnectionKey(thisReplicationTask.getSource(),
          thisReplicationTask.getTarget());
      ReplicationConnection replicationConnection = connectionMap.get(connectionKey.toString());
      if (replicationConnection == null) {
        replicationConnection = new ReplicationConnection();
        replicationConnection.setLocal(connectionKey.getLocal());
        replicationConnection.setRemote(connectionKey.getRemote());
        connectionMap.put(connectionKey.toString(), replicationConnection);
      }

      // find the current ReplicationStats list and create one if none
      List<ReplicationStats> replicationStatsList;

      // is this task a local task or a remote task?
      String taskDirection = toTaskDirection(thisReplicationTask.getSource(),
          thisReplicationTask.getTarget());
      switch (taskDirection) {
        case "local":
          // TODO: only update id if null - and if not null then check equal
          replicationConnection.setLocalTaskId(thisReplicationTask.getReplication_id());
          replicationStatsList = replicationConnection.getLocalActivityList();
          break;
        case "remote":
          // TODO: only update id if null - and if not null then check equal
          replicationConnection.setRemoteTaskId(thisReplicationTask.getReplication_id());
          replicationStatsList = replicationConnection.getRemoteActivityList();
          break;
        default:
          throw new RuntimeException("Unknown taskDirection: " + taskDirection);
      }

      // check the time of the last object and create a new object if the current time has moved past the period accumulated
      ReplicationStats replicationStats = replicationStatsList.size() > 0 ? replicationStatsList
          .get(replicationStatsList.size() - 1) : null;
      if (replicationStats == null || replicationStats.getPeriodEnding().before(new Date())) {
        replicationStats = createReplicationStats();
        addReplicationStats(replicationStatsList, replicationStats);
      }

      // increment the stats for this period by the deltas above
      replicationStats.setDocsRead(replicationStats.getDocsRead() + docsRead);
      replicationStats.setDocsWritten(replicationStats.getDocsWritten() + docsWritten);
      replicationStats
          .setDocWriteFailures(replicationStats.getDocWriteFailures() + docWriteFailures);
    }
  }

  private ConnectionKey toConnectionKey(String source, String target) {

    String localGateway = syncGatewayService.getExportedSyncGatewayDetails().getGatewayURL();

    String local;
    String remote;
    if (source.contains(localGateway) && !target.contains(localGateway)) {
      local = source;
      remote = target;
    } else if (!source.contains(localGateway) && target.contains(localGateway)) {
      local = target;
      remote = source;
    } else {
      throw new RuntimeException(
          "Unexpected situation: neither value equals the local gateway url. " + source + ", "
              + target + " => " + localGateway);
    }

    ConnectionKey connectionKey = new ConnectionKey(local, remote);
    return connectionKey;
  }

  private String toTaskDirection(String source, String target) {
    String taskDirection;
    String localGateway = syncGatewayService.getExportedSyncGatewayDetails().getGatewayURL();
    if (source.contains(localGateway) && !target.contains(localGateway)) {
      taskDirection = "local";
    } else if (!source.contains(localGateway) && target.contains(localGateway)) {
      taskDirection = "remote";
    } else {
      throw new RuntimeException(
          "Unexpected situation: neither value equals the local gateway url. " + source + ", "
              + target + " => " + localGateway);
    }

    return taskDirection;
  }

  private ReplicationStats createReplicationStats() {

    ReplicationStats replicationStats = new ReplicationStats();
    ZonedDateTime zonedDateTime = ZonedDateTime.now().plusMinutes(15);
    replicationStats.setPeriodEnding(Date.from(zonedDateTime.toInstant()));

    return replicationStats;
  }

  private static void addReplicationStats(List<ReplicationStats> replicationStatsList,
      ReplicationStats replicationStats) {
    replicationStatsList.add(replicationStats);
    if (replicationStatsList.size() > 20) {
      replicationStatsList.remove(0);
    }
  }

  public void cancelAllTasks() {
    connectionMap.clear();
    syncGatewayService.cancelAllActiveTasks();
  }

  public void disconnect(ConnectionRequest connectionRequest) {
    logger.info("Disconnect connection request: ");

    // There's a small chance of race problem here, the monitor method could recreate this connection after we've
    // removed it but before we've cancelled it, swapping the order doesn't help.
    ReplicationConnection connection = connectionMap.remove(connectionRequest.getConnectToServer());
    syncGatewayService.cancelTasks(connection.getLocalTaskId(), connection.getRemoteTaskId());
  }

  public void setAsMaster() {
    List<String> keyList = new ArrayList<>(connectionMap.keySet());
    keyList.forEach((connectionKey) -> {
      ConnectionRequest request = new ConnectionRequest();
      request.setConnectToServer(connectionKey);
      disconnect(request);
    });
  }

}

package com.fronde.server.services.couchbase;

import com.fronde.server.domain.ReplicationEntity;
import com.fronde.server.services.application.ApplicationService;
import com.fronde.server.services.application.ApplicationStatus;
import com.fronde.server.services.authentication.MyUser;
import com.fronde.server.services.authentication.MyUserDetailsService;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.utils.JsonUtils;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Component
public class SyncGatewayService {

  private static final Logger logger = LoggerFactory.getLogger(SyncGatewayService.class);


  @Value("${sync.gateway.protocol}")
  protected String syncGatewayProtocol;

  @Value("${sync.gateway.protocol.internal}")
  protected String syncGatewayProtocolInternal;

  @Value("${sync.gateway.host}")
  protected String syncGatewayHost;

  @Value("${sync.gateway.port}")
  protected int syncGatewayPort;

  @Value("${sync.gateway.bucket}")
  protected String syncGatewayBucket;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  @Autowired
  protected ReplicationRepository replicationRepository;

  @Autowired
  protected ApplicationService applicationService;

  @Autowired
  protected MyUserDetailsService myUserDetailsService;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  @Autowired
  protected SGUserService sgUserService;

  @Autowired
  protected RestTemplateBuilder restTemplateBuilder;

  @Autowired
  protected JsonUtils jsonUtils;

  @Autowired
  protected HostnameService hostnameService;


  public ReplicationEntity findReplicationEntity() {

    Optional<ReplicationEntity> optionalReplicationEntity = replicationRepository
        .findById(ReplicationEntity.UNIQUE_ID);
    if (optionalReplicationEntity.isPresent()) {
      return optionalReplicationEntity.get();
    } else {
      ReplicationEntity replicationEntity = new ReplicationEntity();
      replicationEntity.setId(ReplicationEntity.UNIQUE_ID);
      replicationEntity.setDocType("Replication");
      replicationEntity.setModifiedTime(new Date());
      replicationEntity.setBucketIdentifier(objectIdFactory.create());

      replicationRepository.save(replicationEntity);

      logger.info("Created: ReplicationEntity with bucketIdentifier: " + replicationEntity
          .getBucketIdentifier());
      return replicationEntity;
    }
  }

  public String getExportedHostname() {
    return syncGatewayHost.equals("Hostname") ? hostnameService.getHostname() : syncGatewayHost;
  }

  public SyncGatewayDetails getExportedSyncGatewayDetails() {

    String exportedHostname = getExportedHostname();

    ReplicationEntity replicationEntity = findReplicationEntity();
    String bucketIdentifier = replicationEntity.getBucketIdentifier();

    ApplicationStatus status = applicationService.getStatus();
    String version = status.getVersion();
    String environment = status.getEnvironment();

    SyncGatewayDetails details = new SyncGatewayDetails();
    details.setBucketIdentifier(bucketIdentifier);
    details.setSyncGatewayProtocol(syncGatewayProtocol);
    details.setSyncGatewayHost(exportedHostname);
    details.setSyncGatewayPort(syncGatewayPort);
    details.setSyncGatewayBucket(syncGatewayBucket);
    details.setVersion(version);
    details.setEnvironment(environment);

    // TODO: remove this (once authentication is working everywhere)
    details.setRequireAuthentication(true);

    return details;
  }

  public String getLocalGatewayAdminUrl() {
    return syncGatewayProtocolInternal + "://localhost:4985";
  }

  public String getLocalGatewayAdminUrlForReplicate() {
    return getLocalGatewayAdminUrl() + "/_replicate";
  }

  public String getLocalGatewayAdminUrlForActiveTasks() {
    return getLocalGatewayAdminUrl() + "/_active_tasks";
  }


  public Replication[] getActiveTasks() {
    RestTemplate restTemplate = new RestTemplate();
    Replication[] activeTasks = restTemplate
        .getForObject(getLocalGatewayAdminUrlForActiveTasks(), Replication[].class);
    return activeTasks;
  }

  public void cancelAllActiveTasks() {
    Replication[] activeTasks = getActiveTasks();
    for (Replication nextReplication : activeTasks) {
      cancelTask(nextReplication.getReplication_id());
    }
  }

  public void cancelTasks(String... replicationIdList) {
    for (String nextId : replicationIdList) {
      cancelTask(nextId);
    }
  }

  public void cancelTask(String replicationId) {
    logger.info("SyncGatewayService: cancelTask() " + replicationId);
    RestTemplate restTemplate = new RestTemplate();

    ReplicationRequest cancelTask = new ReplicationRequest();
    cancelTask.setReplication_id(replicationId);
    cancelTask.setCancel(true);
    restTemplate.postForObject(getLocalGatewayAdminUrlForReplicate(), cancelTask, Object.class);
  }

  public void createReplication(String replicationId, SyncGatewayDetails source,
      SGAuthenticationUser sourceUser, SyncGatewayDetails target, SGAuthenticationUser targetUser) {

    RestTemplate restTemplate = restTemplateBuilder.build();

    // execute once and cancel any current request
    ReplicationRequest request = new ReplicationRequest(replicationId,
        source.getReplicationUrl(sourceUser), target.getReplicationUrl(targetUser));
    try {
      request.setCancel(true);
      logger.info(
          "Cancel replication:" + redactReplicationRequest(request, sourceUser.getSgPassword(),
              targetUser.getSgPassword()));
      restTemplate.postForObject(getLocalGatewayAdminUrlForReplicate(), request, Object.class);
    } catch (HttpClientErrorException ex) {
      // ignore any of these, we might get one depending on the current state and it doesn't matter if we don't actually cancel anything
    }

    // execute a second time and setup continuous (need to backout the cancel command)
    request.setCancel(null);
    request.setContinuous(true);
    logger.info(
        "Create replication:" + redactReplicationRequest(request, sourceUser.getSgPassword(),
            targetUser.getSgPassword()));
    restTemplate.postForObject(getLocalGatewayAdminUrlForReplicate(), request, Object.class);
  }

  private String redactReplicationRequest(ReplicationRequest replicationRequest, String pwd1,
      String pwd2) {
    String replicationRequestStr = jsonUtils.toJson(replicationRequest);
    replicationRequestStr = replicationRequestStr.replaceAll(pwd1, "**********");
    replicationRequestStr = replicationRequestStr.replaceAll(pwd2, "**********");
    return replicationRequestStr;
  }

  public SGAuthenticationResponse authenticate(SGAuthenticationRequest authenticationRequest) {

    // Load the information we have for this user from our own database (will throw an error if not found)
    MyUser user = (MyUser) myUserDetailsService
        .loadUserByUsername(authenticationRequest.getUsername());

    // If not expired
    if (user.isAccountNonExpired()) {

      // The User object returned had to be part of the Spring Security framework, so it's not obvious but the
      // User object's "password" is actually the hashed value
      String passwordHash = user.getPassword();

      // The incoming password part of the request is a clear text password
      String clearPassword = authenticationRequest.getPassword();

      if (passwordHash == null || clearPassword == null) {
        throw new BadCredentialsException("Not authorised for data replication. Bad credentials.");
      }

      // Do they match?
      boolean matches = passwordEncoder.matches(clearPassword, passwordHash);
      if (matches) {
        SGAuthenticationUser sgUser = sgUserService.createSGUser(authenticationRequest);
        SyncGatewayDetails syncGatewayDetails = getExportedSyncGatewayDetails();

        SGAuthenticationResponse sgAuthenticationResponse = new SGAuthenticationResponse(sgUser,
            syncGatewayDetails);
        return sgAuthenticationResponse;
      } else {
        throw new BadCredentialsException("Not authorised for data replication. Bad credentials.");
      }
    } else {
      throw new AccountExpiredException("Not authorised for data replication. Account expired.");
    }
  }

}

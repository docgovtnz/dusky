package com.fronde.server.services.couchbase;

import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.PublicAPI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/api/replication")
public class ReplicationController {

  @Autowired
  protected ReplicationService replicationService;

  @RequestMapping(value = "/localAddress", method = RequestMethod.GET)
  @ResponseBody
  @PublicAPI
  public LocalAddress getLocalAddress() {
    return replicationService.getLocalAddress();
  }

  @RequestMapping(value = "/syncGatewayStatus", method = RequestMethod.GET)
  @ResponseBody
  @PublicAPI
  public SyncGatewayStatus getSyncGatewayStatus() {
    return replicationService.getSyncGatewayStatus();
  }

  @RequestMapping(value = "/setMode/{mode}", method = RequestMethod.POST)
  @ResponseBody
  public Response setMode(@PathVariable(value = "mode") String mode) {
    return replicationService.setMode(mode);
  }

  @RequestMapping(value = "/connectToNode", method = RequestMethod.POST)
  @ResponseBody
  public Response<ConnectionRequest> connectToNode(
      @RequestBody ConnectionRequest connectionRequest) {
    return replicationService.connectToNode(connectionRequest);
  }

  @RequestMapping(value = "/disconnect", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void disconnect(@RequestBody ConnectionRequest connectionRequest) {
    replicationService.disconnect(connectionRequest);
  }

  @RequestMapping(value = "/allowedTargetAddresses", method = RequestMethod.GET)
  @ResponseBody
  @PublicAPI
  public List<String> getAllowedTargetAddresses() {
    return replicationService.getAllowedTargetAddresses();
  }

}

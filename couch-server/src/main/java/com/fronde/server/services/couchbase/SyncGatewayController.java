package com.fronde.server.services.couchbase;

import com.fronde.server.services.authorization.PublicAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync-gateway")
public class SyncGatewayController {

  @Autowired
  protected SyncGatewayService syncGatewayService;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  @ResponseBody
  @PublicAPI
  public SyncGatewayDetails getSyncGatewayDetails() {
    return syncGatewayService.getExportedSyncGatewayDetails();
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseBody
  @PublicAPI
  public SGAuthenticationResponse authenticate(
      @RequestBody SGAuthenticationRequest authenticationRequest) {
    return syncGatewayService.authenticate(authenticationRequest);
  }
}

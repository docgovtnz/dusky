package com.fronde.server.services.application;

import com.fronde.server.config.CouchbaseStartup;
import com.fronde.server.services.authorization.PublicAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 */
@RestController
@RequestMapping("/api/application")
public class ApplicationController {

  @Autowired
  protected ApplicationService applicationService;

  @Value("${application.mode}")
  protected String serverMode;

  @Autowired
  protected CouchbaseStartup couchbaseStartup;

  @RequestMapping(value = "/status", method = RequestMethod.GET)
  @ResponseBody
  @PublicAPI
  public ApplicationStatus getStatus() {
    ApplicationStatus status = applicationService.getStatus();
    return status;
  }

  @RequestMapping(value = "/reset-indexes", method = RequestMethod.POST)
  @ResponseBody
  @PublicAPI
  public RebuildStatus resetIndexes() {
    if (!"Client".equals(serverMode)) {
      throw new AccessDeniedException("Can only reset indexes when in Client Mode.");
    }
    return couchbaseStartup.resetIndexes();

  }

  @RequestMapping(value = "/reset-indexes-status", method = RequestMethod.GET)
  @ResponseBody
  @PublicAPI
  public RebuildStatus getIndexResetStatus() {
    RebuildStatus status = couchbaseStartup.getRebuildStatus();
    return status;
  }
}

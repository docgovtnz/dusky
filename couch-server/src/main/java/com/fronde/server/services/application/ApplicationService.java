package com.fronde.server.services.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class ApplicationService {

  @Value("${application.version}")
  private String applicationVersion;

  @Value("${application.environment}")
  private String applicationEnvironment;

  public ApplicationStatus getStatus() {
    ApplicationStatus status = new ApplicationStatus();
    status.setVersion(applicationVersion);
    status.setEnvironment(applicationEnvironment);
    return status;
  }

}

package com.fronde.server.services.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Once the failure threshold has been reach both good and bad authentication failure requests are
 * blocked for the timeDelayInSecs period. This will continue for a period of timePeriodInSecs
 * regardless of the username used or the IP address of the submitting request. After
 * timePeriodInSecs the system will go back to having no delay.
 */
@Component
public class AuthenticationFailureBlocker {

  @Value("${authenticationFailure.threshold}")
  protected Integer authenticationFailureThreshold;

  @Value("${authenticationFailure.timePeriodInSecs}")
  protected Integer authenticationFailureTimePeriodInSecs;

  @Value("${authenticationFailure.timeDelayInSecs}")
  protected Integer authenticationFailureTimeDelayInSecs;

  private int currentFailureCount;
  private long lastFailureTime;

  /**
   * This method will watch and count the number of failed authentication requests. If too many
   * failed authentication requests occur within a specified time period then this service will
   * operate in a "go-slow" mode and delay responding by a specified time period.
   *
   * <p>This technique doesn't worry about IP addresses and will delay everyone on the system but
   * valid users will still be able to login ok. It also doesn't
   * lock accounts because that creates an Support desk overhead with people needing to get accounts
   * unlocked and in a worse case scenario it would be relatively easy for an attacker to lock
   * everyone's account and so create a nightmare for the support desk.</p>
   */
  public void trackFailedAuthentications(boolean currentRequestAuthenticatedOk) {

    // track failures and the time of the last failure
    if (!currentRequestAuthenticatedOk) {
      currentFailureCount++;
      lastFailureTime = System.currentTimeMillis();
    }

    // calculate the time since the last failure in milliseconds
    long timeSinceLastFailureInSeconds = (System.currentTimeMillis() - lastFailureTime) / 1000;

    // if the time since the last failure exceeds the time period window then reset the failure count
    if (timeSinceLastFailureInSeconds > authenticationFailureTimePeriodInSecs) {
      currentFailureCount = 0;
    }

    // lots of failures in a short window of time means we start blocking all requests no matter who they are
    if (currentFailureCount > authenticationFailureThreshold) {
      try {
        Thread.sleep(authenticationFailureTimeDelayInSecs * 1000);
      } catch (InterruptedException ex) {
      }
    }
  }

}

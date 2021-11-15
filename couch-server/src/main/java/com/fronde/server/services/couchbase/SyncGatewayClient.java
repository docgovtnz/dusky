package com.fronde.server.services.couchbase;

import com.fronde.server.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The SyncGatewayClient is different from the SyncGatewayController because it makes REST calls to
 * the remote server rather than calling the local server.
 */
@Component
public class SyncGatewayClient {

  private static final Logger logger = LoggerFactory.getLogger(SyncGatewayClient.class);


  @Autowired
  protected JsonUtils jsonUtils;

  public SyncGatewayDetails getSyncGatewayDetails(String serverAddress) {
    String url = "https://" + serverAddress + "/api/sync-gateway/";
    RestTemplate restTemplate = new RestTemplate();
    SyncGatewayDetails syncGatewayDetails = restTemplate
        .getForObject(url, SyncGatewayDetails.class);
    return syncGatewayDetails;
  }

  public SGAuthenticationResponse authenticate(String serverAddress,
      SGAuthenticationRequest authenticationRequest) {
    String url = "https://" + serverAddress + "/api/sync-gateway/";
    try {
      RestTemplate restTemplate = new RestTemplate();
      SGAuthenticationResponse response = restTemplate
          .postForObject(url, authenticationRequest, SGAuthenticationResponse.class);

      logger.info("SG authenticate ok: " + redactAuthenticationResponse(response));
      return response;
    } catch (Exception ex) {
      logger.error("SG authenticate failed: " + url + " - " + authenticationRequest);
      throw new RuntimeException(ex);
    }
  }

  private String redactAuthenticationResponse(SGAuthenticationResponse response) {
    String responseStr = jsonUtils.toJson(response);
    return responseStr.replaceAll(response.getSgAuthenticationUser().getSgPassword(), "**********");
  }

}

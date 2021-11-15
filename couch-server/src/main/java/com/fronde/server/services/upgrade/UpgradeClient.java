package com.fronde.server.services.upgrade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UpgradeClient {

  @Value("${application.auto-connect.host}")
  protected String autoConnectHost;

  @Value("${application.auto-connect.port}")
  protected Integer autoConnectPort;

  private String serverAddress;
  //private String serverAddress = "localhost:8443";
  //private String serverAddress = "trader.base-stack.net:8443";

  public String getServerAddress() {
    if (serverAddress == null) {
      serverAddress = autoConnectHost + ":" + autoConnectPort;
    }
    return serverAddress;
  }

  public FileMetaDataList remoteDownloadList() {
    try {
      String url = "https://" + getServerAddress() + "/api/upgrade/remote/download/list";
      RestTemplate restTemplate = new RestTemplate();
      return restTemplate.getForObject(url, FileMetaDataList.class);
    } catch (Exception ex) {
      // The remote server may not be available, which is ok. Just return an empty object.
      return new FileMetaDataList();
    }
  }

  public byte[] remoteDownloadFile(String file) {
    String url = "https://" + getServerAddress() + "/api/upgrade/remote/download/file/" + file;
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate.postForObject(url, null, byte[].class);
  }

}

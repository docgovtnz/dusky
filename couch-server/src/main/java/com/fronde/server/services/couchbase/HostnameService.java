package com.fronde.server.services.couchbase;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.stereotype.Component;

@Component
public class HostnameService {

  public String getHostname() {
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      return localHost.getHostName();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }
}

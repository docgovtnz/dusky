package com.fronde.server.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

public class ServiceManager {

  /**
   * Logger.
   */
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceManager.class);

  public static final String STOP_SERVICE_SUCCESS_STRING = "was stopped successfully";
  public static final String START_SERVICE_SUCCESS_STRING = "service was started successfully";
  public static final String SET_BINARY_PATH_SUCCESS_STRING = "ChangeServiceConfig SUCCESS";
  public static final String STOP_SERVICE_WASNT_STARTED = "is not started";

  public static final long TIMEOUT_MILLIS = 30000;

  public static final String BINARY_PATH = "BINARY_PATH_NAME";

  /**
   * Stop a service using net stop.
   *
   * @param serviceName The name of the service.
   * @return true on success.
   */
  public boolean stop(String serviceName) {
    logger.info("Stopping Service: " + serviceName);
    CommandLineExecutor stopService = CommandLineExecutor.build("net")
        .argument("stop")
        .argument(serviceName)
        .timeout(TIMEOUT_MILLIS)
        .execute();

    if (stopService.getResultCode() == 0 && stopService.getOutput()
        .contains(STOP_SERVICE_SUCCESS_STRING)) {
      logger.info("Stopped Service: " + serviceName);
      return true;
    } else if (stopService.getResultCode() == 2 && stopService.getOutput()
        .contains(STOP_SERVICE_WASNT_STARTED)) {
      logger.info("Service wasn't started");
      return true;
    } else {
      logger.error("Failed to stop service");
      return false;
    }
  }

  /**
   * Start a service using net start servicename
   *
   * @param serviceName The service name
   * @return true on success.
   */
  public boolean start(String serviceName) {
    logger.info("Starting Service: " + serviceName);
    CommandLineExecutor startService = CommandLineExecutor.build("net")
        .argument("start")
        .argument(serviceName)
        .timeout(TIMEOUT_MILLIS)
        .execute();

    if (startService.getResultCode() == 0 && startService.getOutput()
        .contains(START_SERVICE_SUCCESS_STRING)) {
      logger.info("Started Service: " + serviceName);
      return true;
    } else {
      logger.error("Failed to start service " + serviceName);
      return false;
    }
  }

  /**
   * Get the details of a service using "sc". Be aware that this uses the real service name, not
   * necessarily the name shown in the services panel.
   *
   * @param serviceName The real name of the service.
   * @return true on success.
   */
  public Map<String, String> getServiceDetails(String serviceName) {
    CommandLineExecutor queryService = CommandLineExecutor.build("sc")
        .argument("qc")
        .argument(serviceName)
        .timeout(TIMEOUT_MILLIS)
        .execute();

    if (queryService.getResultCode() == 0) {
      // Parse the output.
      Pattern p = Pattern.compile("^\\s*([^:\\s]*)\\s*:\\s*(.*)$", Pattern.MULTILINE);
      Matcher m = p.matcher(queryService.getOutput());
      Map<String, String> config = new HashMap<>();
      while (m.find()) {
        config.put(m.group(1), m.group(2));
      }
      return config;
    } else {
      return null;
    }
  }

  /**
   * Alters the binary path of a service.
   *
   * @param serviceName The real service name of the service.
   * @param path        The path to use.
   * @return true on success.
   */
  public boolean setBinaryPath(String serviceName, String path) {
    logger.info("Setting binary path for service: " + serviceName);
    CommandLineExecutor configService = CommandLineExecutor.build("sc")
        .argument("config")
        .argument(serviceName)
        .argument("binPath=")
        .argument("\"" + path.replaceAll("\"", "\\\\\"") + "\"")
        .timeout(TIMEOUT_MILLIS)
        .execute();

    if (configService.getResultCode() == 0 && configService.getOutput()
        .contains(SET_BINARY_PATH_SUCCESS_STRING)) {
      logger.info("Binary path changed successfully");
      return true;
    } else {
      logger.info("Failed to change binary path");
      return false;
    }
  }
}

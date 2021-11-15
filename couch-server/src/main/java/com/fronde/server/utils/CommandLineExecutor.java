package com.fronde.server.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Wraps a command line execution.
 */
public class CommandLineExecutor {

  /**
   * Logger.
   */
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommandLineExecutor.class);

  /**
   * Buffer to capture the output.
   */
  private final StringBuffer buffer = new StringBuffer();

  /**
   * Capture the result code from running the process.
   */
  private int resultCode;

  /**
   * The exception, if one is thrown trying to launch the process.
   */
  private Throwable exception;

  /**
   * The command line.
   */
  private final ArrayList<String> commandLine = new ArrayList<>();

  /**
   * The timeout; -1 indicates no timeout.
   */
  private long timeout = -1;

  /**
   * The running process; used to abort if necessary.
   */
  private Process process;

  /**
   * Flag to indicate that the process is running.
   */
  private boolean running = false;

  /**
   * Flag to indicate if the process was aborted due to timeout.
   */
  private boolean timedOut = false;

  /**
   * Create a command line.
   *
   * @param command The command - usually an executable.
   */
  public CommandLineExecutor(String command) {
    commandLine.add(command);
  }

  /**
   * Constructors using a builder pattern.
   *
   * @param command The command - usually an executable.
   * @return
   */
  public static CommandLineExecutor build(String command) {
    return new CommandLineExecutor(command);
  }

  /**
   * Add an argument to the command line.
   *
   * @param argument The argument to add.
   * @return
   */
  public CommandLineExecutor argument(String argument) {
    commandLine.add(argument);
    return this;
  }

  /**
   * Set a timeout.
   *
   * @param timeout The timeout, in milliseconds.
   * @return
   */
  public CommandLineExecutor timeout(long timeout) {
    this.timeout = timeout;
    return this;
  }

  /**
   * Run the process.
   *
   * @return
   */
  public CommandLineExecutor execute() {
    try {
      logger.info("CommandLineExecutor: execute 1");
      logger.info(
          "CommandLineExecutor: " + StringUtils.arrayToDelimitedString(commandLine.toArray(), " "));
      logger.info("CommandLineExecutor: execute 2");
      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder.command(commandLine);
      processBuilder.redirectErrorStream(true);
      running = true;
      process = processBuilder.start();
      StreamGobbler gobbler = new StreamGobbler(process.getInputStream());
      gobbler.start();

      logger.info("CommandLineExecutor: execute 3");

      // Initiate a timeout monitor.
      Timeout timeoutThread = null;
      if (timeout > 0) {
        timeoutThread = new Timeout();
        timeoutThread.monitor();
      }

      this.resultCode = process.waitFor();

      // Mark the process as finished and record if it was aborted
      // due to timeout.
      this.running = false;
      if (this.timedOut) {
        this.resultCode = -99;
      }

      // Stop the timeout thread in case it is still going.
      if (timeoutThread != null) {
        timeoutThread.stop();
      }

      logger.info("Exited with resultCode: " + resultCode);
    } catch (Exception ex) {
      this.resultCode = -100;
      this.exception = ex;
    }
    return this;
  }

  public String getOutput() {
    return buffer.toString();
  }

  public int getResultCode() {
    return this.resultCode;
  }

  public Throwable getException() {
    return this.exception;
  }

  /**
   * Thread to monitor for timeout.
   */
  public class Timeout implements Runnable {

    long startedAt;

    Thread myThread;

    public void run() {
      startedAt = System.currentTimeMillis();

      while ((System.currentTimeMillis() - startedAt) < timeout
          && CommandLineExecutor.this.running) {
        try {
          long timeToGo = startedAt + timeout - System.currentTimeMillis();
          Thread.sleep(timeToGo);
        } catch (InterruptedException ex) {
          // Ignore - we are probably interrupted by the process finishing, but there could be other
          // odd cases, for which we will loop again.
        }
      }
      if (running) {
        logger.error("Process timed out");
        CommandLineExecutor.this.timedOut = true;
        process.destroy();
      } else {
        logger.info("Process finished successfully");
      }
    }

    public void stop() {
      myThread.interrupt();
    }

    public void monitor() {
      myThread = new Thread(this);
      myThread.setDaemon(true);
      myThread.start();
    }
  }

  public class StreamGobbler implements Runnable {

    InputStream inputStream;

    public StreamGobbler(InputStream inputStream) {
      this.inputStream = inputStream;
    }

    public void start() {
      Thread t = new Thread(this);
      t.start();
    }

    public void run() {
      try (
          BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream))
      ) {
        String line = br.readLine();
        while (line != null) {
          buffer.append(line + "\n");
          logger.info(" CMD OUTPUT: " + line);
          line = br.readLine();
        }
      } catch (IOException ex) {
        /* Ignore. */
      }
      logger.info("Finished reading output");
    }

    public String getOutput() {
      return buffer.toString();
    }
  }

}

package com.fronde.server.migration;

import java.util.Date;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class ProgressMonitor {

  private long tick;
  private Class source;

  public void reset(Class source) {
    this.source = source;
    tick = 0;
  }

  public void tick() {
    if (tick % 10000 == 0) {
      System.out.println();
      print("process");
    }
    tick++;
    if (tick % 100 == 0) {
      System.out.print(".");
    }
  }

  public void print(String action) {
    System.out.print(new Date() + ": " + source.getSimpleName() + ": " + action + ": ");
  }

  public void println(String action) {
    print(action);
    System.out.println();
  }

  public void end() {
    System.out.println();
  }
}

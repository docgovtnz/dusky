package com.fronde.server.services.validation;

import org.springframework.context.ApplicationContext;

public class ValidationAppContext {

  private static final ThreadLocal<ApplicationContext> threadLocal = new ThreadLocal<>();

  public static void set(ApplicationContext applicationContext) {
    threadLocal.set(applicationContext);
  }

  public static ApplicationContext get() {
    return threadLocal.get();
  }
}

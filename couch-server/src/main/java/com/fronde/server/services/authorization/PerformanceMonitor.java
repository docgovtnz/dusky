package com.fronde.server.services.authorization;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitor {

  private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);

  @Around("execution(* com.fronde.server.*..*Controller.*(..))")
  public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
    long startAt = System.currentTimeMillis();
    Object retVal = pjp.proceed();
    long endAt = System.currentTimeMillis();
    logger.debug(pjp.getSignature() + " took " + (endAt - startAt) + "ms");

    return retVal;
  }
}

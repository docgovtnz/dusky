package com.fronde.server.services.authorization;

import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 */
@Aspect
@Component
public class CheckPermissionAspect {

  private static final Logger logger = LoggerFactory.getLogger(CheckPermissionAspect.class);

  @Autowired
  protected RoleMap roleMap;

  @Before(value = "@annotation(checkPermission)")
  public void checkPermission(JoinPoint joinPoint, CheckPermission checkPermission) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Permission permission = checkPermission.value();
    Set<Permission> permissionSet = roleMap
        .getPermissionsForAuthorities(authentication.getAuthorities());

    logger.debug("CheckPermission for " + checkPermission.value() + " roles " + authentication
        .getAuthorities() + " with permissions of " + permissionSet);

    if (!permissionSet.contains(permission)) {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      logger.error("Access denied - Username:" + authentication.getPrincipal() + " - Required Role:"
          + checkPermission.value() + " - " + signature.toString());
      throw new AccessDeniedException(
          "You require the '" + permission.userText() + "' permission to perform this action.");
    }
  }

  @Before("execution(* com.fronde.server.*..*Controller.*(..))")
  public void checkDefaultPermission(JoinPoint pjp) {
    MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

    // If the method has a CheckPermission annotation then we can ignore it as it is processed by the
    // aspect above.
    CheckPermission checkPermission = methodSignature.getMethod()
        .getAnnotation(CheckPermission.class);
    if (checkPermission != null) {
      // No need to do anything as the other pointcut will check this.
      return;
    }

    // Check whether the method is annotated as being unauthenticated. If it is NOT, then at least make sure
    // that the user is authenticated.
    PublicAPI publicAPIAnnotation = methodSignature.getMethod().getAnnotation(PublicAPI.class);
    if (publicAPIAnnotation == null) {
      // Check that the user is at least authenticated.
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      Set<Permission> permissionSet = roleMap
          .getPermissionsForAuthorities(authentication.getAuthorities());
      if (!permissionSet.contains(Permission.AUTHENTICATED)) {
        throw new AccessDeniedException("You must be authenticated to perform this action.");
      }
    }
  }
}

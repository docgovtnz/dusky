package com.fronde.server.utils;

import com.fronde.server.services.authentication.MyUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 */
public class SecurityUtils {

  public static GrantedAuthority createAuthority(String simpleRoleName) {
    return new SimpleGrantedAuthority(simpleRoleName);
  }

  public static void assertUserHasRole(String roleName) {
    if (!userHasRole(roleName)) {
      throw new RuntimeException("Not authorized");
    }
  }

  public static boolean userHasRole(String roleName) {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        .contains(createAuthority(roleName));
  }

  public static MyUser currentUser() {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      Object userObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if (userObject instanceof MyUser) {
        return (MyUser) userObject;
      } else {
        throw new RuntimeException(
            "Unexpected state: current principal is not a MyUser but is " + userObject.getClass()
                .getName());
      }
    } else {
      return null;
    }
  }

  public static String currentUsername() {
    return currentUser().getUsername();
  }
}

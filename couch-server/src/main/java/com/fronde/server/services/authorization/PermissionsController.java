package com.fronde.server.services.authorization;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Retrieve the list of permissions for the user.
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {

  @Autowired
  protected RoleMap roleMap;

  @RequestMapping(method = RequestMethod.GET)
  public Set<Permission> getPermissions() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Set<Permission> permissionSet = roleMap
        .getPermissionsForAuthorities(authentication.getAuthorities());
    return permissionSet;
  }

}

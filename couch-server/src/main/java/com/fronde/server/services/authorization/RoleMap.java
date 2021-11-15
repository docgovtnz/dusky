package com.fronde.server.services.authorization;

import com.fronde.server.utils.XmlUtils;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Role/Permission mapping is loaded from resources/security.xml.
 */
@Component
public class RoleMap {

  private Map<String, RoleMapping> map;

  public RoleMap() {
  }

  public Map<String, RoleMapping> getMap() {
    if (map == null) {
      //map = createMap();

      try {
        // Load the map from XML.
        String resourceName = "/security.xml";
        InputStream resourceAsStream = RoleMap.class.getResourceAsStream(resourceName);
        String xml = IOUtils.toString(resourceAsStream);
        RoleList roles = XmlUtils.convertFromString(RoleList.class, xml);
        Map<String, RoleMapping> map = new HashMap<>();
        for (RoleMapping mapping : roles.getRoles()) {
          map.put(mapping.getRole(), mapping);
        }
        this.map = map;
      } catch (Exception ex) {
        throw new RuntimeException("Unable to load secruity roles", ex);
      }
    }
    return map;
  }

  public Set<Permission> getPermissionsForAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    String[] roles = authorities.stream().map(GrantedAuthority::getAuthority)
        .toArray(String[]::new);
    Set<Permission> permissionSet = getPermissionsForRoles(roles);
    return permissionSet;
  }

  public Set<Permission> getPermissionsForRoles(String... roleName) {
    Set<Permission> permissionSet = new HashSet<>();
    for (String nextRoleName : roleName) {
      buildPermissionSet(permissionSet, nextRoleName);
    }
    return permissionSet;
  }

  public void buildPermissionSet(Set<Permission> permissionSet, String roleName) {
    RoleMapping mapping = getMap().get(roleName);
    if (mapping != null) {
      permissionSet.addAll(mapping.getPermissions());
      if (!StringUtils.isEmpty(mapping.getExtendsRole())) {
        buildPermissionSet(permissionSet, mapping.getExtendsRole());
      }
    }
  }

  public int getRoleLevel() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    OptionalInt maxLevel = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .mapToInt(roleName -> this.getMap().get(roleName).getLevel()).max();
    return maxLevel.isPresent() ? maxLevel.getAsInt() : -1;
  }

  public int getRoleLevel(String role) {
    return StringUtils.isEmpty(role) ? -1 : this.getMap().get(role).getLevel();
  }

}

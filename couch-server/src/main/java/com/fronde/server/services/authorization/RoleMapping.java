package com.fronde.server.services.authorization;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleMapping {

  @XmlAttribute(name = "name")
  private String role;

  @XmlAttribute(name = "extends")
  private String extendsRole;

  @XmlAttribute(name = "level")
  private int level;

  @XmlElement(name = "permission")
  private List<Permission> permissions;

  public RoleMapping() {
  }

  public RoleMapping(String role, Permission... permissions) {
    this.role = role;
    addPermissions(permissions);
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public void addPermissions(Permission... permissions) {
    getPermissions().addAll(Arrays.asList(permissions));
  }

  public List<Permission> getPermissions() {
    if (permissions == null) {
      permissions = new ArrayList<>();
    }
    return permissions;
  }

  public void setPermissions(List<Permission> permissions) {
    this.permissions = permissions;
  }

  public String getExtendsRole() {
    return extendsRole;
  }

  public void setExtendsRole(String extendsRole) {
    this.extendsRole = extendsRole;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}

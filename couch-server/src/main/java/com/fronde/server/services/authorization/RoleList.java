package com.fronde.server.services.authorization;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "roles")
public class RoleList {

  @XmlElement(name = "role")
  private List<RoleMapping> roles;

  public List<RoleMapping> getRoles() {
    return this.roles;
  }

}

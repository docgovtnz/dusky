package com.fronde.server.services.authentication;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 *
 */
public class MyUser extends User {

  private String personId;

  public MyUser(String username, String password,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }

  public MyUser(String username, String password, boolean enabled, boolean accountNonExpired,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, enabled, accountNonExpired, true, true, authorities);
  }

  public MyUser(String username, String password, boolean enabled, boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities);
  }

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }
}

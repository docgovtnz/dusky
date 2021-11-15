package com.fronde.server.services.authentication;

import com.fronde.server.config.ApplicationStartup;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SystemAuthentication {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

  public interface Activity {

    void doAsUser();
  }

  public static void doAsUser(String username, String role, Activity function) {
    SystemInternalAuthentication auth = buildAuthentication(username, role);
    SecurityContext origCtx = SecurityContextHolder.getContext();

    try {
      logger.info("Escalating privilege to " + role + " to invoke function");
      SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
      SecurityContextHolder.getContext().setAuthentication(auth);
      function.doAsUser();
    } finally {
      SecurityContextHolder.setContext(origCtx);
      logger.info("Restoring original security context");
    }
  }

  private static SystemInternalAuthentication buildAuthentication(String username, String role) {
    SystemInternalAuthentication auth = new SystemInternalAuthentication();
    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role));
    auth.authorities = authorities;
    auth.principal = new MyUser(username, "<none>", authorities);
    return auth;
  }


  public static class SystemInternalAuthentication implements Authentication {

    private MyUser principal;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
    }

    @Override
    public Object getCredentials() {
      return null;
    }

    @Override
    public Object getDetails() {
      return null;
    }

    @Override
    public Object getPrincipal() {
      return principal;
    }

    @Override
    public boolean isAuthenticated() {
      return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
      throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public String getName() {
      return "SYSTEM";
    }
  }
}

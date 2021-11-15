package com.fronde.server.services.authentication;

import com.fronde.server.domain.CredentialEntity;
import com.fronde.server.domain.PersonEntity;
import com.fronde.server.services.credential.CredentialService;
import com.fronde.server.services.person.PersonService;
import com.fronde.server.utils.SecurityUtils;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

  @Autowired
  protected PersonService personService;

  @Autowired
  protected CredentialService credentialService;

  /**
   *
   */
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    logger.info("loadUserByUsername(): " + username);
    PersonEntity person = personService.findPersonByUsername(username);
    if (person != null) {

      logger.info("loadUserByUsername(): found person " + person.getId());

      Set<GrantedAuthority> authorities = new HashSet<>();
      String role = person.getPersonRole();
      if (role != null) {
        authorities.add(SecurityUtils.createAuthority(role));
        logger.info("loadUserByUsername(): found authorities " + authorities);
      }

      CredentialEntity credential = credentialService.findByPersonId(person.getId());
      String passwordHash = credential != null ? credential.getPasswordHash() : null;
      logger.info("loadUserByUsername(): found passwordHash " + (passwordHash != null));

      boolean accountNonExpired = true;
      if (person.getAccountExpiry() != null) {
        // deem the account as expired if the expiry date is before the start of today
        // example expiry = 2/8 and today is 3/8 => not expired
        // example expiry = 2/8 and today is 2/8 => not expired
        // example expiry = 2/8 and today is 1/8 => expired
        boolean accountExpired = person.getAccountExpiry()
            .before(Date.from(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant()));
        accountNonExpired = !accountExpired;
      }

      logger.info("loadUserByUsername(): found accountNonExpired " + accountNonExpired);

      MyUser userDetails = new MyUser(username, passwordHash, person.getHasAccount(),
          accountNonExpired, authorities);
      userDetails.setPersonId(person.getId());

      return userDetails;
    } else {
      String msg = "Unable to find person with username of " + username;
      logger.error(msg);
      throw new UsernameNotFoundException(msg);
    }
  }
}

package com.fronde.server.services.authentication;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.services.person.PersonService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenService {

  // The only good secret is the one that you have never told anyone. Rather than keeping the secret in a property
  // file this approach will randomly generate a fresh secret each time the server is restarted. This
  // will invalidate any existing tokens and force users to login again, an alternative approach would be to use
  // public/private key signing saveRevision each public key in the database and include the id of the trusted public key
  // in the token so that this service could validate previously generated keys (because it trusts anything in
  // the database).

  @Value("${jwt.useRandomSecret}")
  private final boolean useRandomSecret = true;
  private byte[] secret;

  @Value("${jwt.token.expirationInSecs}")
  private final long expirationInSecs = 10 * 60 * 60;

  private static final String CLAIM_KEY_AUTHORITIES = "authorities";
  private static final String CLAIM_NAME = "name";
  private static final String CLAIM_PERSON_ID = "personId";

  private final SecureRandom secureRandom = new SecureRandom();

  @Autowired
  protected PersonService personService;

  private synchronized byte[] getSecret() {
    if (secret == null) {
      if (useRandomSecret) {
        secret = new byte[128];
        secureRandom.nextBytes(secret);
      } else {
        secret = "secret".getBytes();
      }
    }
    return secret;
  }

  private String getAuthoritiesAsCSV(Collection<? extends GrantedAuthority> authoritiesCollection) {
    return StringUtils.collectionToCommaDelimitedString(authoritiesCollection);
  }

  private Collection<GrantedAuthority> getAuthoritiesAsCollection(String authoritiesCSV) {
    String[] authoritiesArray = StringUtils.commaDelimitedListToStringArray(authoritiesCSV);
    Collection<GrantedAuthority> authoritiesCollection = new ArrayList<>();
    for (String nextAuthority : authoritiesArray) {
      authoritiesCollection.add(new SimpleGrantedAuthority(nextAuthority));
    }

    return authoritiesCollection;
  }

  public UserDetails parseToken(String token) {

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring("Bearer ".length());
    }

    Jws<Claims> claims = Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(token);

    Date expirationDate = claims.getBody().getExpiration();
    if (expirationDate.before(new Date())) {
      throw new SessionAuthenticationException("Token expired");
    }

    String principal = claims.getBody().getSubject();
    String credentials = "XXX";  // we don't actually know the credentials, and we'll erase them as well below...

    String authoritiesCSV = claims.getBody().get(CLAIM_KEY_AUTHORITIES, String.class);
    Collection<GrantedAuthority> authoritiesCollection = getAuthoritiesAsCollection(authoritiesCSV);

    String personId = claims.getBody().get(CLAIM_PERSON_ID, String.class);
    PersonEntity person = personService.findById(personId);
    if (person == null) {
      // this situation can happen in development environments when the JWT token is reused from one data migration
      // to another, or from one environment to another. If the personID does not exist it's important to force
      // the user to login again because otherwise the revision's will be marked with an invalid personId.
      throw new SessionAuthenticationException(
          "PersonId is no longer available. Please login again: " + personId);
    }

    MyUser user = new MyUser(principal, credentials, authoritiesCollection);
    user.setPersonId(personId);
    user.eraseCredentials();

    return user;
  }

  public String generateToken(PersonEntity person, UserDetails userDetails) {
    return Jwts.builder()
        .setSubject(person.getUserName())
        .setExpiration(new Date(System.currentTimeMillis() + expirationInSecs * 1000))
        .signWith(SignatureAlgorithm.HS512, getSecret())
        .claim(CLAIM_KEY_AUTHORITIES, getAuthoritiesAsCSV(userDetails.getAuthorities()))
        .claim(CLAIM_NAME, person.getName())
        .claim(CLAIM_PERSON_ID, person.getId())
        .compact();
  }

}

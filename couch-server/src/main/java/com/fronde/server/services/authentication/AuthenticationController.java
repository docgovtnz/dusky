package com.fronde.server.services.authentication;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.services.authorization.PublicAPI;
import com.fronde.server.services.couchbase.TaskAutoConnect;
import com.fronde.server.services.person.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authenticate")
public class AuthenticationController {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired
  protected AuthenticationManager authenticationManager;

  @Autowired
  protected JwtTokenService tokenService;

  @Autowired
  protected UserDetailsService userDetailsService;

  @Autowired
  protected PersonService personService;

  @Autowired
  protected TaskAutoConnect taskAutoConnect;

  @Autowired
  protected AuthenticationFailureBlocker authenticationFailureBlocker;

  @RequestMapping(method = RequestMethod.POST)
  @PublicAPI
  public ResponseEntity<?> authenticationRequest(
      @RequestBody AuthenticationRequest authenticationRequest) throws AuthenticationException {

    boolean authenticationOk = false;
    try {
      logger.info("authenticationRequest() for: " + authenticationRequest.getUsername());

      // Perform the authentication
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authenticationRequest.getUsername(),
              authenticationRequest.getPassword()
          )
      );

      logger.info("authenticationRequest() authenticated ok: ");
      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserDetails userDetails = this.userDetailsService
          .loadUserByUsername(authenticationRequest.getUsername());
      logger.info("authenticationRequest() loadUserByUsername() userDetails = : " + userDetails
          .getUsername() + " - " + userDetails.getAuthorities());

      PersonEntity person = personService.findPersonByUsername(authenticationRequest.getUsername());
      logger.info("authenticationRequest() findPersonByUsername() person = : " + person.getId());

      String token = tokenService.generateToken(person, userDetails);
      logger.info("authenticationRequest() generateToken() ok =  " + (token != null));

      // On login check the data replication connection
      taskAutoConnect.onLoginCheckDataReplication();
      logger.info("authenticationRequest() onLoginCheckDataReplication() ok");

      authenticationOk = true;

      // Return the token
      return ResponseEntity.ok(new AuthenticationResponse(token));
    } catch (Exception ex) {
      logger.error(
          "authenticationRequest() failed: " + ex.getClass().getName() + " - " + ex.getMessage());
      throw ex;
    } finally {
      authenticationFailureBlocker.trackFailedAuthentications(authenticationOk);
    }
  }

}

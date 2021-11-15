package com.fronde.server.services.couchbase;

import com.fronde.server.utils.JsonUtils;
import com.fronde.server.utils.SSLUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class SGUserService {

  private static final Logger logger = LoggerFactory.getLogger(SGUserService.class);

  private static final String DATE_FORMAT = "yyyy-MM-dd-HHmmss";

  @Autowired
  protected JsonUtils jsonUtils;

  @Value("${sync.gateway.protocol.internal}")
  protected String syncGatewayProtocolInternal;

  private String getEndpoint() {
    // Must be localhost because Admin port only accepts local connections
    return syncGatewayProtocolInternal + "://" + "localhost:4985" + "/kbird";
  }


  public SGAuthenticationUser createSGUser(SGAuthenticationRequest authenticationRequest) {

    String username = authenticationRequest.getUsername();
    String password = UUID.randomUUID().toString();

    createUser(username, password);

    SGAuthenticationUser sgAuthenticationUser = new SGAuthenticationUser(username, password);
    return sgAuthenticationUser;
  }

  public SGAuthenticationUser createSGUser(SGAuthenticationUser sgUser) {
    String username = sgUser.getSgUsername();
    String password = sgUser.getSgPassword();

    createUser(username, password);

    return sgUser;
  }

  /**
   * If we want to revoke a laptop's connection on AWS we'll need to reset the Laptop's password in
   * the database AND on the SG. This method does the SG bit, it resets the password to a random
   * value and disables the user.
   * <p>
   * At the moment there are not separate actions for resetting a Laptop's password or revoking a
   * Laptop's access but it doesn't matter. We'll disable the user here, but if the Laptop is being
   * reset instead of revoked then the login process will change disabled back to false.
   *
   * @param username
   */
  public void resetSGUser(String username) {
    // This password is random and thrown away, in a reset scenario it's different to the actual laptop password
    // but that doesn't matter. If the Laptop successfully authenticates with the Dusky Application/Database password
    // then createSGUser() will create a new temporary password and change the disabled status back to enabled.
    String password = UUID.randomUUID().toString();
    UserRequest userRequest = new UserRequest(username, password, new String[]{"*"}, new String[]{},
        null, true);
    logger.info("resetSGUser(): " + jsonUtils.toJson(redactAuthenticationRequest(userRequest)));
    postUserRequest(userRequest);
  }

  private void createUser(String username, String password) {
    UserRequest userRequest = new UserRequest(username, password, new String[]{"*"}, new String[]{},
        null, false);
    logger.info("createSGUser(): " + jsonUtils.toJson(redactAuthenticationRequest(userRequest)));
    postUserRequest(userRequest);
  }

  private void postUserRequest(UserRequest userRequest) {
    String postUrl = getEndpoint() + "/_user/" + userRequest.getName();
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.put(postUrl, userRequest);
  }

  private UserRequest redactAuthenticationRequest(UserRequest userRequest) {
    UserRequest clone = jsonUtils.toObject(jsonUtils.toJson(userRequest), UserRequest.class);
    clone.setPassword("*************");
    return clone;
  }

  public void deleteUser(String username) {
    RestTemplate restTemplate = new RestTemplate();
    String deleteUrl = getEndpoint() + "/_user/" + username;
    restTemplate.delete(deleteUrl);
  }

  public String[] getAllUsers() {
    RestTemplate restTemplate = new RestTemplate();
    String getUrl = getEndpoint() + "/_user/";
    ResponseEntity<String> forEntity = restTemplate.getForEntity(getUrl, String.class);
    String httpResponseBody = forEntity.getBody();
    String[] userArray = jsonUtils.toObject(httpResponseBody, String[].class);
    return userArray;
  }

  public void cleanup() {
    // get all users
    String[] users = getAllUsers();

    logger.info("Current SG user count = " + users.length);

    Date startOfToday = getStartOfToday();
    logger.info("Start of day " + startOfToday);

    DateFormat dateFormat = getDateFormat();

    // for each user
    for (int i = 0; i < users.length; i++) {
      String nextUser = users[i];
      try {
        // find the time of that user
        Date userDate = dateFormat.parse(nextUser);

        // if user time < expiry time then
        if (userDate.before(startOfToday)) {
          // delete the user
          logger.info("Remove: " + nextUser + " - " + userDate);
          deleteUser(nextUser);
        } else {
          // keep the user
          logger.info("Keep: " + nextUser);
        }
      } catch (ParseException ex) {
        logger.warn("ParseException for user = " + nextUser + ", " + ex.getMessage());
      }
    }
  }

  private DateFormat getDateFormat() {
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    return dateFormat;
  }

  Date getStartOfToday() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  public static void main(String[] args) throws Exception {

    SSLUtils.trustAllCertificates();

    SGUserService userService = new SGUserService();
    userService.jsonUtils = new JsonUtils();

    userService.cleanup();
  }


  /**
   * DTO class for REST API, try not to use this outside of this class.
   */
  private static class UserRequest {

    private String name;
    private String password;
    private String[] admin_channels;
    private String[] admin_roles;
    private String email;
    private Boolean disabled = false;

    public UserRequest() {
    }

    public UserRequest(String name, String password, String[] admin_channels, String[] admin_roles,
        String email, Boolean disabled) {
      this.name = name;
      this.password = password;
      this.admin_channels = admin_channels;
      this.admin_roles = admin_roles;
      this.email = email;
      this.disabled = disabled;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String[] getAdmin_channels() {
      return admin_channels;
    }

    public void setAdmin_channels(String[] admin_channels) {
      this.admin_channels = admin_channels;
    }

    public String[] getAdmin_roles() {
      return admin_roles;
    }

    public void setAdmin_roles(String[] admin_roles) {
      this.admin_roles = admin_roles;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public Boolean getDisabled() {
      return disabled;
    }

    public void setDisabled(Boolean disabled) {
      this.disabled = disabled;
    }
  }
}

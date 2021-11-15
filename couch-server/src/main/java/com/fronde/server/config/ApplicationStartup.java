package com.fronde.server.config;

import com.fronde.server.config.syncgateway.SyncGatewayInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

  // Admin user creation is now disabled, but please leave commented out code for a year or so just in case.
  //@Value("${createAdminUser.enabled}")
  //protected boolean createAdminUserEnabled;

  //@Value("${createAdminUser.username}")
  //protected String username;

  //@Value("${createAdminUser.password}")
  //protected String password;

  //@Autowired
  //protected PersonService personService;

  //@Autowired
  //protected CredentialService credentialsService;

  @Autowired
  protected CouchbaseStartup couchbaseStartup;

  //@Autowired
  //protected SyncGatewayService syncGatewayService;

  @Autowired
  protected SettingStartup settingStartup;

  @Autowired
  protected SyncGatewayInitializer syncServerInitializer;

  /**
   *
   */
  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {

    syncServerInitializer.initialize();

    // Pump this once to create the ReplicationEntity if needed
    //syncGatewayService.findReplicationEntity();

    // Admin user creation is now disabled, but please leave commented out code for a year or so just in case.
    // Create the Admin user if required...
        /*if(createAdminUserEnabled) {
            if(!adminUserExists()) {
                logger.warn("Admin user does not exist so creating it now. See application.properties for details.");
                SystemAuthentication.doAsUser("INTERNAL", "System Admin", () -> createAdminUser());
            }
            else {
                logger.warn("Admin user already exists so skipping creation.");
            }
        }
        else {
            logger.warn("Admin user creation not enabled.");
        }*/
    logger.warn("Admin user creation is now disabled.");

    // Create any indexes and rebuild if required...
    couchbaseStartup.init();

    settingStartup.createSettings();

    logger.info(String.valueOf(System.getProperties()));
  }

  // Admin user creation is now disabled, but please leave commented out code for a year or so just in case.

    /*private boolean adminUserExists() {
        try {
            return personService.findPersonByUsername(username) != null;
        }
        catch (Exception ex) {
            return false;
        }
    }*/

    /*private void createAdminUser() {
        PersonEntity person = new PersonEntity();
        person.setId(PersonConstants.UNIQUE_ADMIN_ID);
        person.setDocType("Person");
        person.setName("Admin");
        person.setUserName("Admin");
        person.setPersonRole("System Admin");
        person.setHasAccount(true);
        person.setCurrentCapacity("DOC");
        person.setPhoneNumber("000 000 0000");
        person.setEmailAddress("dummy@dummy.dummy");

        Response<PersonEntity> response = personService.save(person);

        try { Thread.sleep(2000); } catch (InterruptedException ex){}

        if(response.getMessages().size() == 0) {
            person = response.getModel();

            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
            resetPasswordRequest.setUsername(person.getUserName());

            ResetPasswordResponse resetPasswordResponse = credentialsService.resetPassword(resetPasswordRequest);
            String resetPassword = resetPasswordResponse.getNewPassword();

            try { Thread.sleep(2000); } catch (InterruptedException ex){}

            // Doing change password after a reset seems odd but it was so that the Admin password could always be
            // the same and controlled by the properties file whenever creating a new environment.
            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setUsername(person.getUserName());
            changePasswordRequest.setCurrentPassword(resetPassword);
            changePasswordRequest.setNewPassword(password);
            credentialsService.changePassword(changePasswordRequest);
        }
        else {
            throw new RuntimeException("Admin person save failed validation: " + response.getMessages());
        }
    }*/

}

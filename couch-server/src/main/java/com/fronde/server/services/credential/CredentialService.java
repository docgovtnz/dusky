package com.fronde.server.services.credential;

import com.fronde.server.config.EnvironmentProcessor;
import com.fronde.server.domain.CredentialEntity;
import com.fronde.server.domain.PersonEntity;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.authorization.RoleMap;
import com.fronde.server.services.authorization.RoleTypes;
import com.fronde.server.services.common.ObjectIdFactory;
import com.fronde.server.services.couchbase.SGUserService;
import com.fronde.server.services.person.PersonService;
import com.fronde.server.utils.BaseEntityUtils;
import com.fronde.server.utils.SecurityUtils;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CredentialService {

  @Autowired
  protected PasswordEncoder passwordEncoder;

  @Autowired
  protected PasswordGenerator passwordGenerator;

  @Autowired
  protected PersonService personService;

  @Autowired
  protected CredentialRepository credentialRepository;

  @Autowired
  protected RoleMap roleMap;

  @Autowired
  protected ObjectIdFactory objectIdFactory;

  @Autowired
  protected SGUserService sgUserService;

  public CredentialEntity findByPersonId(String personId) {
    return credentialRepository.findOneByPersonId(personId);
  }

  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    String username = changePasswordRequest.getUsername();
    String currentPassword = changePasswordRequest.getCurrentPassword();
    String newPassword = changePasswordRequest.getNewPassword();

    // Security check - make sure that we are resetting the password of a lower levelled user.
    PersonEntity person = personService.findPersonByUsername(username);
    int targetRoleLevel = roleMap.getRoleLevel(person.getPersonRole());
    int currentRoleLevel = roleMap.getRoleLevel();
    if (targetRoleLevel > currentRoleLevel) {
      throw new AccessDeniedException(
          "You do not have permission to reset the password for a user with the role '" + person
              .getPersonRole() + "'");
    }

    String personId = person.getId();
    if (person != null) {
      CredentialEntity credential = credentialRepository.findOneByPersonId(personId);
      if (credential != null) {
        boolean matches = passwordEncoder.matches(currentPassword, credential.getPasswordHash());
        if (matches) {
          saveCredentialsWithPersonId(personId, newPassword);
        } else {
          throw new RuntimeException("Invalid password");
        }
      } else {
        throw new RuntimeException("Unable to find existing person credentials for: " + username);
      }
    } else {
      throw new RuntimeException("Unable to find person for: " + username);
    }
  }


  public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {

    // TODO: security checks
    // authenticated user can change their own password (this isn't a reset)
    // administrator can reset anyone's password
    // non-authenticated user can send an email reset link (implemented elsewhere)
    // Additional validation.
    String currentUsername = SecurityUtils.currentUsername();
    String resetUsername = resetPasswordRequest.getUsername();

    // If we are trying to reset ourselves, then we don't need additional checks.
    PersonEntity person = personService.findPersonByUsername(resetUsername);
    if (person != null) {
      if (!Objects.equals(resetUsername, currentUsername)) {
        // We need the reset password permission.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<Permission> permissionSet = roleMap
            .getPermissionsForAuthorities(authentication.getAuthorities());
        if (!permissionSet.contains(Permission.PASSWORD_RESET)) {
          throw new AccessDeniedException(
              "You do not have permission to reset other user's passwords");
        }

        // Check the level of the person we are trying to reset.
        if (person == null) {
          throw new RuntimeException(
              "Unable to find person for username: " + resetPasswordRequest.getUsername());
        } else {
          int targetRoleLevel = roleMap.getRoleLevel(person.getPersonRole());
          int currentRoleLevel = roleMap.getRoleLevel();
          if (targetRoleLevel > currentRoleLevel) {
            throw new AccessDeniedException(
                "You do not have permission to reset the password for a user with the role '"
                    + person.getPersonRole() + "'");
          }
        }
      }

      // If this is a Data Replication account then need to reset password against the SG before saving to database
      // Otherwise the new password might get replicated (not that it would matter too much since the password
      // in the database is only a hashed value.
      if (person.getPersonRole().equals(RoleTypes.Data_Replication)) {
        sgUserService.resetSGUser(resetUsername);
      }

      deleteAllCredentials(person.getId());

      String newPassword = passwordGenerator.generateRandomPassword();
      saveCredentialsWithPersonId(person.getId(), newPassword);

      ResetPasswordResponse response = new ResetPasswordResponse();
      // Either set the encrypted password or the new clear password (but not both)
      if (person.getPersonRole().equals(RoleTypes.Data_Replication)) {
        response.setEncryptedPassword(EnvironmentProcessor.encrypt(newPassword));
      } else {
        response.setNewPassword(newPassword);
      }

      return response;
    } else {
      throw new RuntimeException("Unable to find person: " + resetUsername);
    }
  }

  private void deleteAllCredentials(String personId) {
    List<CredentialEntity> list = credentialRepository.findAllByPersonId(personId);
    list.forEach((credentialEntity) -> {
      credentialRepository.delete(credentialEntity);
    });
  }


  /**
   * As part of saving a person this method will be called to saveRevision the password. If the
   * password is specified then it is converted into a hash value and saved in a separate
   * PersonCredentials object for security reasons so there is less chance that database password
   * hash values can be exposed to the web tier.
   *
   * <p>This method should remain private so that change/reset password comes through the public
   * methods and applies
   * the various business rules required.</p>
   */
  private void saveCredentialsWithPersonId(@NotNull String personId, String passwordClear) {

    if (personId == null) {
      throw new NullPointerException("personId must not be null");
    }

    if (passwordClear != null) {

      // TODO: validate the clear text password for length/complexity rules here...

      String credentialId;
      if (personId.equals("ADMIN_ID")) {
        credentialId = "ADMIN_CREDENTIAL_ID";
      } else {
        credentialId = objectIdFactory.create();
      }

      CredentialEntity credential = new CredentialEntity();
      credential.setId(credentialId);
      credential.setDocType("Credential");
      credential.setPersonId(personId);

      String passwordHash = passwordEncoder.encode(passwordClear);
      credential.setPasswordHash(passwordHash);

      BaseEntityUtils.setModifiedFields(credential);

      credentialRepository.save(credential);
    }
  }
}


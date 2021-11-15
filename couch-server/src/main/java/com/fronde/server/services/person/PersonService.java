package com.fronde.server.services.person;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.RoleMap;
import com.fronde.server.services.validation.ValidationMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PersonService extends PersonBaseService {

  @Autowired
  private RoleMap roleMap;

  @Value("${application.mode}")
  protected String applicationMode;

  public PersonEntity findPersonByUsername(String username) {
    return repository.findOneByUserName(username);
  }

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO dto = new DeleteByIdCheckDTO();
    dto.setId(docId);
    // we can't delete something that doesn't exist
    dto.setDeleteOk(docId != null && !repository.hasReferences(docId));
    return dto;
  }

  @Override
  public Response<PersonEntity> save(PersonEntity entity) {
    int userLevel = roleMap.getRoleLevel();
    int targetLevel = -1;

    // If the application mode is not Server then don't allow editing or saving of people with the Data Replication role
    if (!"Server".equals(applicationMode)) {
      String currentRole = null;
      if (entity.getId() != null) {
        Optional<PersonEntity> currentPerson = repository.findById(entity.getId());
        if (currentPerson.isPresent()) {
          currentRole = currentPerson.get().getPersonRole();
        }
      }
      String newRole = entity.getPersonRole();
      if ("Data Replication".equals(currentRole) || "Data Replication".equals(newRole)) {
        return buildSecurityResponse(entity,
            "You can only edit a user with the role of 'Data Replication' on the Server node. You are currently on a '"
                + applicationMode + "' node");
      }
    }

    // If we are editing an existing person, make sure that they are not a person with a higher role than us.
    if (entity.getId() != null) {
      Optional<PersonEntity> currentPerson = repository.findById(entity.getId());
      if (currentPerson.isPresent()) {
        int currentLevel = roleMap.getRoleLevel(currentPerson.get().getPersonRole());
        if (currentLevel > userLevel) {
          return buildSecurityResponse(entity,
              "You do not have permission to edit a user with the current role of '" + currentPerson
                  .get().getPersonRole() + "'");
        }
      }
    }

    // Make sure we are not trying to escalate someone's privilege higher than our role.
    if (roleMap.getRoleLevel(entity.getPersonRole()) > userLevel) {
      return buildSecurityResponse(entity,
          "You do not have permissions to save a user with the role '" + entity.getPersonRole()
              + "'");
    }

    return super.save(entity);
  }

  /**
   * Convenience method to create a security response.
   *
   * @param entity  The entity to return.
   * @param message The error message.
   * @return A Response object.
   */
  private Response<PersonEntity> buildSecurityResponse(PersonEntity entity, String message) {
    ValidationMessage msg = new ValidationMessage();
    msg.setMessageText(message);
    Response<PersonEntity> response = new Response<>();
    response.getMessages().add(msg);
    response.setModel(entity);
    return response;
  }

  public void export(PersonCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "Name", "Phone Number", "Account", "Expired", "Current Capacity",
        "Access Level", "Expiry Date");
    List<String> props = Arrays.asList(
        "name", "phoneNumber", "hasAccount", "expired", "currentCapacity",
        "personRole", "accountExpiry");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<PersonSearchDTO> pr = this.findSearchDTOByCriteria(criteria);
    exportUtils.export(response, pr, header, props, "person");
  }

  public PagedResponse<PersonSearchDTO> findSearchDTOByCriteria(PersonCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
  }

  /**
   * Returns true if the specified person name is unique for the specified person (via personID).
   * personID can be null in the case of this being a new person.
   *
   * @param personID
   * @param name
   * @return
   */
  public boolean isUniqueName(String personID, String name) {
    boolean exists = repository.existsByNameExcluding(name, personID);
    return !exists;
  }

  /**
   * Returns true if the specified person username is unique for the specified person (via
   * personID). personID can be null in the case of this being a new person.
   *
   * @param personID
   * @param username
   * @return
   */
  public boolean isUniqueUsername(String personID, String username) {
    boolean exists = repository.existsByUsernameExcluding(username, personID);
    return !exists;
  }

}

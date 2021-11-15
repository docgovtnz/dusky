package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.services.authorization.RoleTypes;
import com.fronde.server.services.validation.ValidationResultFactory;

public class UsernameRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    PersonEntity pe = (PersonEntity) object;
    if (propertyValue != null) {
      if (pe.getPersonRole() != null && pe.getPersonRole().equals(RoleTypes.Data_Replication)) {
        // If account is "Data Replication" then it can't have any dots in the username
        boolean containsDots = pe.getUserName() != null && pe.getUserName().contains(".");
        if (containsDots) {
          resultFactory.addResult("ContainsDots").with("userName", pe.getUserName());
        }
      }
    }
  }
}

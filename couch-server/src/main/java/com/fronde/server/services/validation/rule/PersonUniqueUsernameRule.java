package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.services.person.PersonService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;
import org.springframework.util.StringUtils;

public class PersonUniqueUsernameRule extends AbstractRule<String> {

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    PersonEntity r = (PersonEntity) object;

    // if we are validating a new person, we will still have a PersonEntity object, just that r.id will be null

    // we don't care when the property value is empty (should be caught by a mandatory rule if required)

    if (!StringUtils.isEmpty(propertyValue) && !isUniqueUsername(r.getId(), propertyValue)) {
      resultFactory.addResult("PersonUsernameNotUnique");
    }
  }

  protected boolean isUniqueUsername(String personID, String username) {
    PersonService service = ValidationAppContext.get().getBean(PersonService.class);
    return service.isUniqueUsername(personID, username);
  }

}

package com.fronde.server.services.person;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.options.PersonSummaryDTO;
import java.util.List;

public interface PersonRepositoryCustom {

  PersonEntity findOneByUserName(String name);

  PagedResponse<PersonEntity> findByCriteria(PersonCriteria criteria);

  List<String> findUsers();

  boolean hasReferences(String personID);

  List<PersonSummaryDTO> findPersonSummaries();

  PagedResponse<PersonSearchDTO> findSearchDTOByCriteria(PersonCriteria criteria);

  /**
   * Returns whether the person with the specific name exists, excluding if it is the specified
   * person (using excludingPersonID)
   *
   * @param name
   * @param excludingPersonID
   * @return
   */
  boolean existsByNameExcluding(String name, String excludingPersonID);

  /**
   * Returns whether the person with the specific username exists, excluding if it is the specified
   * person (using excludingPersonID)
   *
   * @param username
   * @param excludingPersonID
   * @return
   */
  boolean existsByUsernameExcluding(String username, String excludingPersonID);

}

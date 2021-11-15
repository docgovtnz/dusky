package com.fronde.server.services.person;

import com.fronde.server.domain.PersonEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface PersonRepository extends PagingAndSortingRepository<PersonEntity, String>,
    PersonRepositoryCustom {

}

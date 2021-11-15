package com.fronde.server.services.credential;

import com.fronde.server.domain.CredentialEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface CredentialRepository extends PagingAndSortingRepository<CredentialEntity, String>,
    CredentialRepositoryCustom {

}

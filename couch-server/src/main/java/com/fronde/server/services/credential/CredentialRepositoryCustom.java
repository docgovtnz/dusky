package com.fronde.server.services.credential;

import com.fronde.server.domain.CredentialEntity;
import java.util.List;

public interface CredentialRepositoryCustom {

  List<CredentialEntity> findAllByPersonId(String personId);

  CredentialEntity findOneByPersonId(String personId);

}

package com.fronde.server.services.snarkimport;

import com.fronde.server.domain.SnarkImportEntity;
import com.fronde.server.domain.response.PagedResponse;
import java.util.List;

public interface SnarkImportRepositoryCustom {

  List<SnarkImportEntity> findBySnarkFileHash(String hash);

  PagedResponse<SnarkImportEntity> findByCriteria(SnarkImportCriteria criteria);

  List<SnarkImportEntity> findBySnarkFile(String snarkFile);

}

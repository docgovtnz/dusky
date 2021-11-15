package com.fronde.server.services.transmitter;

import com.fronde.server.domain.TransmitterEntity;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
@N1qlPrimaryIndexed
public interface TransmitterRepository extends
    PagingAndSortingRepository<TransmitterEntity, String>, TransmitterRepositoryCustom {

}

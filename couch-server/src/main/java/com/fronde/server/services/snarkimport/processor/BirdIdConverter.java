package com.fronde.server.services.snarkimport.processor;

import com.fronde.server.domain.BirdEntity;
import java.util.List;

public interface BirdIdConverter {

  List<BirdEntity> convertUhfId(String island, int uhfId);

}

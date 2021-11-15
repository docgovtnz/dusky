package com.fronde.server.services.sample;

import com.fronde.server.domain.SampleEntity;
import com.fronde.server.domain.response.PagedResponse;
import java.util.List;

public interface SampleRepositoryCustom {

  PagedResponse<SampleEntity> findByCriteria(SampleCriteria criteria);

  PagedResponse<SampleSearchDTO> findSearchDTOByCriteria(SampleCriteria criteria);

  List<String> findOtherSampleTypes();

  List<String> findSpermDiluents();

  List<String> findSampleCategories();

  List<String> findSampleTypes();

  List<TestStatsDTO> getHaematologyTestsStats(String type);

  List<ResultRankDTO> getHaematologyTestsRanks(String type, List<ResultDTO> results);

  List<TestStatsDTO> getChemistryAssaysStats(String type);

  List<ResultRankDTO> getChemistryAssaysRanks(String type, List<ResultDTO> results);

}

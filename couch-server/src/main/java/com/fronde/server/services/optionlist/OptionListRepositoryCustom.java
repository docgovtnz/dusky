package com.fronde.server.services.optionlist;

import com.fronde.server.domain.OptionListEntity;
import com.fronde.server.domain.response.Response;
import java.util.List;
import java.util.Map;

public interface OptionListRepositoryCustom {

  Map<String, List<String>> getOptions();

  Map<String, String> getOptionListTitles();

  Response<OptionListEntity> saveList(OptionListEntity newList);

  List<String> findDisplayOptionsByCriteria(OptionListCriteria criteria);

}

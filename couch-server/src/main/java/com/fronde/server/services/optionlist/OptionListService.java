package com.fronde.server.services.optionlist;

import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OptionListService extends OptionListBaseService {

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    return null;
  }

  public List<String> findDisplayOptionsByCriteria(OptionListCriteria criteria) {
    return repository.findDisplayOptionsByCriteria(criteria);
  }

}

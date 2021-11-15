package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.NoraNetCmLongEntity;
import com.fronde.server.domain.NoraNetEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.List;

public class NoraNetCheckMateRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    NoraNetEntity entity = (NoraNetEntity) object;
    List<NoraNetCmLongEntity> cmLongList = entity.getCmLongList();
    if (cmLongList != null && cmLongList.size() > 0) {
      boolean femaleMissing = false;
      for (NoraNetCmLongEntity cmLong : cmLongList) {
        if (cmLong != null) {
          // Each cmLong must have at least 1 female record
          if (cmLong.getCmFemaleList() == null || cmLong.getCmFemaleList().size() < 1) {
            femaleMissing = true;
          }
        }
      }
      if (femaleMissing) {
        resultFactory.addResult("NoraNetCmFemaleRequired");

      }
    }
  }

}

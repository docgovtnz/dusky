package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.PersonEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.springframework.util.StringUtils;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonHasAccountEnabled extends AbstractRule<Boolean> {

  @Override
  public void validate(Object object, Boolean propertyValue,
      ValidationResultFactory resultFactory) {
    PersonEntity pe = (PersonEntity) object;
    if (propertyValue != null) {
      if (propertyValue) {
        if (StringUtils.isEmpty(pe.getUserName())) {
          resultFactory.addResult("UsernameRequired");
        }
        if (StringUtils.isEmpty(pe.getPersonRole())) {
          resultFactory.addResult("RoleRequired");
        }
        // hard coded "Volunteer" as there didn't seem to be a suitable place to extract this value from configuration or elsewhere
        if (pe.getAccountExpiry() == null && "Volunteer".equals(pe.getCurrentCapacity())) {
          resultFactory.addResult("AccountExpiryRequired");
        }
        // hard coded "Volunteer" as there didn't seem to be a suitable place to extract this value from configuration or elsewhere
        Date startOfToday = Date
            .from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        // the account expiry must be set to after today or the account must be disabled to be resaved
        if (pe.getAccountExpiry() != null && pe.getAccountExpiry().before(startOfToday)) {
          resultFactory.addResult("FutureAccountExpiryRequired");
        }
      }
    }
  }
}

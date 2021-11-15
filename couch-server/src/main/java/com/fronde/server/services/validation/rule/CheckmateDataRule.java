package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.CheckmateDataEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckmateDataRule extends AbstractRule<Object> {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    RecordEntity r = (RecordEntity) object;

    // If there are checkmate records, then validate the required fields are set.
    if (r.getCheckmate() != null && r.getCheckmate().getCheckmateDataList() != null
        && r.getCheckmate().getCheckmateDataList().size() > 0) {

      boolean isErrol = "From Errol".equals(r.getCheckmate().getDataCaptureType());
      for (CheckmateDataEntity row : r.getCheckmate().getCheckmateDataList()) {
        if (isErrol) {
          validateRequired(resultFactory, "FemaleChannel", row.getFemaleTx());
          validateRequired(resultFactory, "Time", row.getTime());
          validateRequired(resultFactory, "Duration", row.getDuration());
          validateRequired(resultFactory, "Quality", row.getQuality());

        } else {
          validateRequired(resultFactory, "FemaleChannel1", row.getFemaleTx1());
          validateRequired(resultFactory, "FemaleChannel2", row.getFemaleTx2());
          validateRequired(resultFactory, "Time1", row.getTime1());
          validateRequired(resultFactory, "Time2", row.getTime2());
          validateRequired(resultFactory, "Duration1", row.getDuration1());
          validateRequired(resultFactory, "Duration2", row.getDuration2());
          validateRequired(resultFactory, "Quality1", row.getQuality1());
          validateRequired(resultFactory, "Quality2", row.getQuality2());
        }
      }
    }
  }

  protected void validateRequired(ValidationResultFactory resultFactory, String key, Object value) {
    if (value == null) {
      resultFactory.addResult(key + ".required");
    }
  }
}

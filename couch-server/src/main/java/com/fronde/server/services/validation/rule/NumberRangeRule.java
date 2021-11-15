package com.fronde.server.services.validation.rule;

import com.fronde.server.services.validation.ValidationResultFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class NumberRangeRule extends AbstractRule {

  @XmlAttribute
  private Double min;

  @XmlAttribute
  private Double max;

  @XmlAttribute
  private Boolean wholeNumber;


  public Double getMin() {
    return min;
  }

  public void setMin(Double min) {
    this.min = min;
  }

  public Double getMax() {
    return max;
  }

  public void setMax(Double max) {
    this.max = max;
  }

  public Boolean getWholeNumber() {
    return wholeNumber;
  }

  public void setWholeNumber(Boolean wholeNumber) {
    this.wholeNumber = wholeNumber;
  }

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    if (propertyValue != null) {
      if (propertyValue instanceof Number) {

        double doubleValue = ((Number) propertyValue).doubleValue();

        if (min != null) {
          if (doubleValue < min) {
            resultFactory.addResult("LessThanMinValue").with("min", min);
          }
        }

        if (max != null) {
          if (doubleValue > max) {
            resultFactory.addResult("GreaterThanMaxValue").with("max", max);
          }
        }

        if (wholeNumber) {
          if ((doubleValue != Math.floor(doubleValue)) && !Double.isInfinite(doubleValue)) {
            resultFactory.addResult("WholeNumber");
          }
        }
      } else {
        resultFactory.addResult("NotANumber");
      }
    }
  }
}

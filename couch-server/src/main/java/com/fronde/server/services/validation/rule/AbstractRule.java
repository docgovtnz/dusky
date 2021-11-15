package com.fronde.server.services.validation.rule;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fronde.server.services.validation.ValidationResultFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
    @JsonSubTypes.Type(value = NumberRangeRule.class, name = "NumberRangeRule"),
    @JsonSubTypes.Type(value = RequiredRule.class, name = "RequiredRule")}
)
public abstract class AbstractRule<T> {

  public abstract void validate(Object object, T propertyValue,
      ValidationResultFactory resultFactory);

  public boolean anyNotNullOrNotEmpty(Object... objects) {
    for (Object o : objects) {
      if ((o != null) && (!(o instanceof String) || !((String) o).isEmpty())) {
        return true;
      }
    }
    return false;
  }

  public interface Test {

    boolean test();
  }

  protected void validateRequiredIf(ValidationResultFactory resultFactory, Test test, Object value,
      String fieldName, int row) {
    if (test.test()) {
      validateRequired(resultFactory, value, fieldName, row);
    }
  }

  protected void validateRequiredIf(ValidationResultFactory resultFactory, Test test, Object value,
      String fieldName) {
    if (test.test()) {
      validateRequired(resultFactory, value, fieldName);
    }
  }

  protected void validateRequired(ValidationResultFactory resultFactory, Object value,
      String fieldName) {
    if (value == null) {
      resultFactory.addResult("required")
          .with("field", fieldName);
    }
  }

  protected void validateRequired(ValidationResultFactory resultFactory, Object value,
      String fieldName, int row) {
    if (value == null) {
      resultFactory.addResult("requiredForRow")
          .with("field", fieldName)
          .with("row", row);
    }
  }
}

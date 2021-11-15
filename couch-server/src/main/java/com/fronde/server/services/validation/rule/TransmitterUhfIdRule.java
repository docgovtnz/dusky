package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation rules for UHF ID field on Add/Edit Transmitter page
 *
 * @version 1.0
 * @date 25/05/2021
 */
public class TransmitterUhfIdRule extends AbstractRule<String> {

  private static final String REGEX = "^([0-9]|[1-9][0-9]|[1-9][0-9][0-9])$";
  private final Pattern pattern = Pattern.compile(REGEX);

  @Override
  public void validate(Object object, String propertyValue, ValidationResultFactory resultFactory) {
    TransmitterEntity entity = (TransmitterEntity) object;
    Integer uhfId = entity.getUhfId();

    // validate UHF ID to be in the range of (0 ~ 999)
    Optional.ofNullable(uhfId).ifPresent(id -> {
      if (!isInRange(id)) {
        resultFactory.addResult("TransmitterUhfIdNotInRange");
      }
    });
  }

  private boolean isInRange(Integer uhfId) {
    Matcher matcher = pattern.matcher(uhfId.toString());
    return matcher.matches();
  }
}

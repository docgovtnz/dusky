package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.FeedOutEntity;
import com.fronde.server.domain.FoodTallyEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.ArrayList;
import java.util.List;

public class FeedOutRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    FeedOutEntity feedOut = (FeedOutEntity) object;

    // Date in <= Date out (if both have values)
    if ((feedOut.getDateIn() != null) && (feedOut.getDateOut() != null)) {
      if (feedOut.getDateOut().after(feedOut.getDateIn())) {
        resultFactory.addResult("FeedOutDateOutOnOrBeforeDateIn");
      }
    }

    // In <= Out (if both have values)
    if (feedOut.getFoodTallyList() != null) {

      List<String> rows = new ArrayList<>();

      // Check the items.
      for (int i = 0; i < feedOut.getFoodTallyList().size(); i++) {
        int row = i + 1;
        FoodTallyEntity foodTally = feedOut.getFoodTallyList().get(i);

        // Validate the dates make sense.
        if (foodTally.getIn() != null && foodTally.getOut() != null) {

          if (foodTally.getIn() > foodTally.getOut()) {
            rows.add("" + row);
          }
        }
      }

      if (!rows.isEmpty()) {
        resultFactory.addResult("FeedOutInLessOrEqualOut")
            .with("rows", rows);

      }
    }
  }
}

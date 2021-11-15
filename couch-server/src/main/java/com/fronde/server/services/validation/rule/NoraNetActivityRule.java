package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.NoraNetCmShortEntity;
import com.fronde.server.domain.NoraNetDetectionEntity;
import com.fronde.server.domain.NoraNetEggTimerEntity;
import com.fronde.server.domain.NoraNetEntity;
import com.fronde.server.domain.NoraNetStandardEntity;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class NoraNetActivityRule extends AbstractRule {

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {
    NoraNetEntity entity = (NoraNetEntity) object;
    List<NoraNetDetectionEntity> detectionList = entity.getDetectionList();
    if (detectionList != null && detectionList.size() > 0) {
      List<String> requiredActivities = new ArrayList<>();
      for (NoraNetDetectionEntity detect : detectionList) {
        if (detect != null) {
          // If category starts with "D" i.e. "Detection" then activity must be null
          if ("D".equalsIgnoreCase(detect.getCategory().substring(0, 1))) {
            if (detect.getActivity() != null) {
              resultFactory.addResult("NoraNetActivityNotRelevant")
                  .with("detectionType", detect.getCategory());
            }
            // If category is NOT "Detection" then activity must be provided
          } else {
            if (detect.getActivity() == null) {
              requiredActivities.add(detect.getCategory());
            }
          }
          if (detect.getActivity() != null) {
            detect.setActivity(roundActivityHours(detect.getActivity()));
          }
        }
      }
      if (!requiredActivities.isEmpty()) {
        resultFactory.addResult("NoraNetActivityRequired")
            .with("detectionTypes", requiredActivities);
      }
    }

    List<NoraNetStandardEntity> standardList = entity.getStandardList();
    if (standardList != null && standardList.size() > 0) {
      for (NoraNetStandardEntity standard : standardList) {
        if (standard != null) {
          if (standard.getActivity() != null) {
            standard.setActivity(roundActivityHours(standard.getActivity()));
          }
        }
      }
    }

    List<NoraNetEggTimerEntity> eggTimerList = entity.getEggTimerList();
    if (eggTimerList != null && eggTimerList.size() > 0) {
      for (NoraNetEggTimerEntity eggTimer : eggTimerList) {
        if (eggTimer != null) {
          if (eggTimer.getActivity() != null) {
            eggTimer.setActivity(roundActivityHours(eggTimer.getActivity()));
          }
        }
      }
    }

    List<NoraNetCmShortEntity> cmShortList = entity.getCmShortList();
    if (cmShortList != null && cmShortList.size() > 0) {
      for (NoraNetCmShortEntity cmShort : cmShortList) {
        if (cmShort != null) {
          if (cmShort.getActivity() != null) {
            cmShort.setActivity(roundActivityHours(cmShort.getActivity()));
          }
        }
      }
    }

  }

  private Float roundActivityHours(Float activity) {
    if (activity == 0) {
      return activity;
    }
    double activityDouble = activity.doubleValue();
    BigDecimal activityDec = new BigDecimal(activityDouble).setScale(1, RoundingMode.HALF_UP);
    return activityDec.floatValue();
  }
}

package com.fronde.server.services.validation.rule;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.LocationEntity;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.validation.ValidationAppContext;
import com.fronde.server.services.validation.ValidationResultFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.util.StringUtils;

public class LocationTypeTargetBirdRule extends AbstractRule {

  private final Set<String> targetBirdIsMandatorySet = new HashSet<>();
  private final Set<String> handraiseSet = new HashSet<>();


  public LocationTypeTargetBirdRule() {
    targetBirdIsMandatorySet.addAll(Arrays.asList("Brooder", "Incubator", "Nest", "Pen"));
    handraiseSet.addAll(Arrays.asList("Brooder", "Incubator", "Pen"));
  }

  @Override
  public void validate(Object object, Object propertyValue, ValidationResultFactory resultFactory) {

    LocationEntity location = (LocationEntity) object;
    String locationType = location.getLocationType();

    // This rule only applies when the location type is supplied (it's mandatory anyway)
    if (!StringUtils.isEmpty(locationType)) {

      // When entering a new Nest/Incubator/Brooder/Pen location type, the Target Bird should be mandatory.
      String targetBirdID = location.getBirdID();
      if (targetBirdIsMandatorySet.contains(locationType) && StringUtils.isEmpty(targetBirdID)) {
        resultFactory.addResult("TargetBirdMandatoryForThisLocationType")
            .with("locationType", locationType);
      }

      // For incubators, brooders, and pens, the user should be choosing "Handraise" as the target bird.
      if (handraiseSet.contains(locationType) && !StringUtils.isEmpty(targetBirdID)) {

        // find the Bird
        BirdEntity targetBird = getBirdByID(targetBirdID);

        // If the Bird's name is not Handraise
        if (targetBird == null || StringUtils.isEmpty(targetBird.getBirdName()) || !targetBird
            .getBirdName().equals("Handraise")) {
          resultFactory.addResult("TargetBirdHandraiseForThisLocationType")
              .with("locationType", locationType);
        }
      }
    }
  }

  protected BirdEntity getBirdByID(String birdID) {
    BirdService birdService = ValidationAppContext.get().getBean(BirdService.class);
    BirdEntity bird = birdService.findById(birdID);
    return bird;
  }

}

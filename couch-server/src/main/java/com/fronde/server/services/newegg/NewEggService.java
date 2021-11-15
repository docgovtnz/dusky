package com.fronde.server.services.newegg;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.EggMeasurementsEntity;
import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.NewEggEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.lifestage.LifeStageService;
import com.fronde.server.services.location.LocationService;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.services.validation.ValidationService;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewEggService {

  @Autowired
  private ValidationService validationService;

  @Autowired
  private BirdService birdService;

  @Autowired
  private LocationService locationService;

  @Autowired
  private LifeStageService lifeStageService;

  public Response<NewEggEntity> save(NewEggEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      BirdEntity b = new BirdEntity();
      b.setCurrentLocationID(entity.getLocationID());
      LocationEntity l = locationService.findById(b.getCurrentLocationID());
      b.setLayLocationID(l.getId());
      b.setCurrentIsland(l.getIsland());
      // clutch is detail held on the nest (which is a type of location)
      // every new clutch gets a new nest even if it is at the same location
      if (l.getNestDetails() != null) {
        b.setClutch(l.getNestDetails().getClutch());
      }
      // lay island is needed as current island may change in future
      b.setLayIsland(l.getIsland());
      b.setNestMother(entity.getMother());
      b.setBirdFeatureList(Collections.emptyList());
      b.setBirdName(entity.getEggName());
      // egg name exists because bird name will be changed eventually
      b.setEggName(entity.getEggName());
      // clutch order is derived in the UI from the number of birds associated with the location
      // since there is a new location for each clutch then the number of eggs currently in the clutch
      // matches the number of birds associated with the location through bird.currentLocationID
      b.setClutchOrder(entity.getClutchOrder());
      // TODO determine whether to save the calculated fresh weight field (impact on graphs - possibly)
      // as per statement from Anton: 'Just confirming that “actual fresh weight” on the egg is not usually populated, so we can call this a legacy field'
      // ignore b.setActualFreshWeight() as it is a legacy field
      b.setDateLaid(entity.getLayDate());
      EggMeasurementsEntity eggMeasurements = new EggMeasurementsEntity();
      eggMeasurements.setEggLength(entity.getEggLength());
      eggMeasurements.setEggWidth(entity.getEggWidth());
      eggMeasurements.setFwCoefficientX104(entity.getFwCoefficientX10P4());
      b.setEggMeasurements(eggMeasurements);
      // ignore b.setLayYear() as this can be derived from the lay date
      b.setMother(entity.getMother());
      // a new egg is neither fertile or infertile so set viable to null
      b.setViable(null);
      // ignore b.setWeight() as this is a legacy field. The weights attached to records will be used going forward
      // ignore b.setFirstDayAtOrVeryCloseToNest() for now. TODO this is something to do with chicks
      // new field added to capture data entered for a new egg
      b.setLayDateIsEstimate(entity.getLayDateIsEstimate());
      // set alive to true initially so that is shows up as a bird of interest using default search parameters
      b.setAlive(true);
      Response<BirdEntity> r = birdService.save(b);
      if (!r.getMessages().isEmpty()) {
        messages = r.getMessages();
      } else {
        entity.setBirdID(r.getModel().getId());
        LifeStageEntity ls = new LifeStageEntity();
        ls.setDateTime(entity.getLayDate());
        ls.setAgeClass("Egg");
        ls.setBirdID(r.getModel().getId());
        Response<LifeStageEntity> lsr = lifeStageService.save(ls);
        if (r.getMessages().isEmpty()) {
          messages = lsr.getMessages();
        }
      }
    }

    Response<NewEggEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

}

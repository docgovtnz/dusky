package com.fronde.server.services.deadembryo;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.DeadEmbryoEntity;
import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.WeightEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.lifestage.LifeStageService;
import com.fronde.server.services.location.LocationService;
import com.fronde.server.services.record.RecordService;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.services.validation.ValidationService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeadEmbryoService {

  @Autowired
  protected ValidationService validationService;

  @Autowired
  private BirdService birdService;

  @Autowired
  private RecordService recordService;

  @Autowired
  private LocationService locationService;

  @Autowired
  private LifeStageService lifeStageService;

  public Response<DeadEmbryoEntity> save(DeadEmbryoEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      BirdEntity b = birdService.findById(entity.getBirdID());
      b.setAlive(false); // TODO this should be done by record save
      b.setEggMeasurements(entity.getEggMeasurements());
      b.setEmbryoMeasurements(entity.getEmbryoMeasurements());
      b.setDemise(entity.getDate());
      // TODO add comments and append into bird comments
      Response<BirdEntity> br = birdService.save(b);
      if (!br.getMessages().isEmpty()) {
        messages = br.getMessages();
      } else {
        RecordEntity r = new RecordEntity();

        // Set the location.
        LocationEntity l = locationService.findById(b.getCurrentLocationID());
        r.setLocationID(b.getCurrentLocationID());
        r.setEasting(l.getEasting());
        r.setNorthing(l.getNorthing());
        r.setMappingMethod(l.getMappingMethod());
        r.setSignificantEvent(true);
        r.setIsland(l.getIsland());

        // Save final weight as this could be useful for analysis of records
        WeightEntity w = new WeightEntity();
        if(entity.getEggMeasurements().getEggWeightBeforeFillingWithWater() != null) {
          w.setWeight(entity.getEggMeasurements().getEggWeightBeforeFillingWithWater() / 1000);
        }
        r.setWeight(w);
        r.setRecordType("Capture");
        r.setReason("Dead");
        r.setObserverList(entity.getObserverList());
        r.setEntryDateTime(new Date());
        r.setActivity("None");

        // TODO change to date time; BB-date/time from Death Record.
        r.setDateTime(new Date());

        r.setBirdID(entity.getBirdID());
        // TODO populate with comments also r.setComments();
        // don't populate r.setMeasureDetail(); as measurements taken are for the dead bird not alive ones
        // don't populate r.setWeight(); as weight for a dead embryo are not as useful on a graph
        Response<RecordEntity> rr = recordService.save(r);
        if (!rr.getMessages().isEmpty()) {
          messages = rr.getMessages();
        } else {
          // no changes to life stage
        }
      }
    }

    Response<DeadEmbryoEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

}

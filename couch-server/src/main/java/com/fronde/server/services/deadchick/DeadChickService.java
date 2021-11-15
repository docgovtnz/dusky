package com.fronde.server.services.deadchick;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.DeadChickEntity;
import com.fronde.server.domain.LocationEntity;
import com.fronde.server.domain.RecordEntity;
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
public class DeadChickService {

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

  public Response<DeadChickEntity> save(DeadChickEntity entity) {
    // TODO remove this button. Have users enter via a record instead
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      BirdEntity b = birdService.findById(entity.getBirdID());
      b.setAlive(false); // TODO should this be done when saving the record?
      Response<BirdEntity> br = birdService.save(b);
      if (!br.getMessages().isEmpty()) {
        messages = br.getMessages();
      } else {
        RecordEntity r = new RecordEntity();
        r.setBirdID(entity.getBirdID());

        LocationEntity l = locationService.findById(b.getCurrentLocationID());
        r.setIsland(l.getIsland()); // TODO confirm
        r.setMappingMethod(l.getMappingMethod()); // TODO confirm
        r.setLocationID(b.getCurrentLocationID()); // TODO confirm
        r.setEasting(l.getEasting()); // TODO confirm
        r.setNorthing(l.getNorthing()); // TODO confirm

        r.setDateTime(entity.getDate());

        r.setActivity("Unknown"); // TODO
        r.setRecordType("Capture"); // TODO confirm
        r.setReason("Dead"); // TODO confirm
        r.setSubReason(null); // TODO confirm
        r.setSignificantEvent(true);  // TODO confirm
        r.setObserverList(entity.getObserverList()); // TODO confirm

        r.setEntryDateTime(new Date()); // TODO confirm
        r.setComments(null); // TODO confirm
        // TODO confirm
        // r.setMeasureDetail();
        // TODO confirm
        // r.setWeight();
        Response<RecordEntity> rr = recordService.save(r);
        if (!rr.getMessages().isEmpty()) {
          messages = rr.getMessages();
        }
      }
    }

    Response<DeadChickEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

}

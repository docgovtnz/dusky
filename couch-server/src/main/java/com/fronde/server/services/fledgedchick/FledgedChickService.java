package com.fronde.server.services.fledgedchick;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.FledgedChickEntity;
import com.fronde.server.domain.LifeStageEntity;
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
public class FledgedChickService {

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

  public Response<FledgedChickEntity> save(FledgedChickEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      BirdEntity b = birdService.findById(entity.getBirdID());
      b.setFledged(true);
      b.setDateFledged(entity.getDate());
      Response<BirdEntity> br = birdService.save(b);

      if (!br.getMessages().isEmpty()) {
        messages = br.getMessages();
      } else {
        RecordEntity r = new RecordEntity();
        r.setBirdID(entity.getBirdID());

        LocationEntity l = locationService.findById(b.getCurrentLocationID());
        r.setIsland(l.getIsland());
        r.setMappingMethod(l.getMappingMethod());
        r.setLocationID(b.getCurrentLocationID());
        r.setEasting(l.getEasting());
        r.setNorthing(l.getNorthing());

        // just a date is fine however we do give user option to enter a time
        //TODO Change this to be a date/time.
        r.setDateTime(entity.getDate());

        // TODO add activity as a field on the fledge;BB-yes, do this.
        r.setActivity("Unknown"); // Leave as Unknown as this was what is against other records
        r.setRecordType("Encounter");
        r.setReason("Fledged");
        r.setSubReason(null);
        r.setSignificantEvent(true);
        r.setObserverList(entity.getObserverList());

        r.setEntryDateTime(new Date());
        r.setComments(null);
        // straight forward record, no other information to capture
        Response<RecordEntity> rr = recordService.save(r);
        if (!rr.getMessages().isEmpty()) {
          messages = rr.getMessages();
        } else {
          // TODO this should probably be in record save
          LifeStageEntity ls = new LifeStageEntity();
          ls.setAgeClass("Chick");
          ls.setBirdID(b.getId());
          ls.setRecordID(r.getId());
          // TODO ls.setChangeType();
          ls.setDateTime(entity.getDate());
          lifeStageService.saveWithThrow(ls);
          // TODO check for errors
        }
      }
    }

    Response<FledgedChickEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

}

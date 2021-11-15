package com.fronde.server.services.hatch;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.HatchEntity;
import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.lifestage.LifeStageService;
import com.fronde.server.services.location.LocationService;
import com.fronde.server.services.record.RecordService;
import com.fronde.server.services.validation.ValidationMessage;
import com.fronde.server.services.validation.ValidationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HatchService {

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

  public Response<HatchEntity> save(HatchEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      BirdEntity b = birdService.findById(entity.getBirdID());
      b.setDateHatched(entity.getHatchDate());
      Response<BirdEntity> br = birdService.save(b);
      if (!br.getMessages().isEmpty()) {
        messages = br.getMessages();
      } else {
        // no record required
        // TODO this should probably be in record save
        LifeStageEntity ls = new LifeStageEntity();
        ls.setAgeClass("Chick");
        ls.setBirdID(b.getId());
        ls.setChangeType("Nest Observation");
        ls.setDateTime(entity.getHatchDate());
        lifeStageService.saveWithThrow(ls);
        // TODO check for errors
      }
    }

    Response<HatchEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

}

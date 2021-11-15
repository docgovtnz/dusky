package com.fronde.server.services.fertileegg;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.FertileEggEntity;
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
public class FertileEggService {

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

  public Response<FertileEggEntity> save(FertileEggEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      BirdEntity b = birdService.findById(entity.getBirdID());
      // set alive to true even though it is probably already set to true as per new egg rules
      b.setAlive(true);
      b.setFather(entity.getFather());
      b.setDefiniteFather(entity.getDefiniteFather());
      b.setViable("fert");
      Response<BirdEntity> br = birdService.save(b);
      if (!br.getMessages().isEmpty()) {
        messages = br.getMessages();
      } else {
        // there is no record to be created for fertile
        // there is no lifestage update for fertile
      }
    }

    Response<FertileEggEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

}


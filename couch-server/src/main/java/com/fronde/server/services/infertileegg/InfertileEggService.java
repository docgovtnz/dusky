package com.fronde.server.services.infertileegg;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.InfertileEggEntity;
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
public class InfertileEggService {

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

  public Response<InfertileEggEntity> save(InfertileEggEntity entity) {
    List<ValidationMessage> messages = validationService.validateEntity(entity);
    if (messages.size() == 0) {
      BirdEntity b = birdService.findById(entity.getBirdID());
      b.setAlive(false);
      b.setViable("infert");
      b.setEggMeasurements(entity.getEggMeasurements());
      Response<BirdEntity> br = birdService.save(b);
      if (!br.getMessages().isEmpty()) {
        messages = br.getMessages();
      } else {
        // no record or lifestage to save for infertile
      }
    }

    Response<InfertileEggEntity> response = new Response<>(entity);
    response.setMessages(messages);
    return response;
  }

}


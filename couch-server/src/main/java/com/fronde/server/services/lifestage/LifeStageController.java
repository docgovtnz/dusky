package com.fronde.server.services.lifestage;

import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/lifestage")
public class LifeStageController extends LifeStageBaseController {

  @RequestMapping(value = "/birdID/{docId}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.LIFESTAGE_VIEW)
  public LifeStageEntity findByBirdID(@PathVariable(value = "docId") String docId) {
    throw new UnsupportedOperationException("TODO: not sure that this is being used at the moment");
  }

}

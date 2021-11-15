package com.fronde.server.services.fledgedchick;

import com.fronde.server.domain.FledgedChickEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/fledgedchick")
public class FledgedChickController {

  @Autowired
  private FledgedChickService service;

  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.BIRD_EDIT)
  public Response<FledgedChickEntity> save(@RequestBody FledgedChickEntity entity) {
    return service.save(entity);
  }

}

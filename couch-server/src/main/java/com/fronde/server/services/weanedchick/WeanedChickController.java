package com.fronde.server.services.weanedchick;

import com.fronde.server.domain.WeanedChickEntity;
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
@RequestMapping("/api/weanedchick")
public class WeanedChickController {

  @Autowired
  private WeanedChickService service;

  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.BIRD_EDIT)
  public Response<WeanedChickEntity> save(@RequestBody WeanedChickEntity entity) {
    return service.save(entity);
  }

}

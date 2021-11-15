package com.fronde.server.services.island;

import com.fronde.server.domain.IslandEntity;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/island")
public class IslandController extends IslandBaseController {

  @RequestMapping(value = "/", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.ISLAND_VIEW)
  public List<IslandEntity> findAll() {
    return service.findAll();
  }

  @RequestMapping(value = "/name/{island}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.ISLAND_VIEW)
  public IslandEntity findByName(@PathVariable(value = "island") String island) {
    return service.findByName(island);
  }

}

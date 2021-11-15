package com.fronde.server.services.meta;

import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.meta.domain.MetaClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meta")
public class MetaDataController {

  @Autowired
  protected MetaDataService metaDataService;

  @RequestMapping(value = "/{entityClassName}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public MetaClass getMetaClass(@PathVariable(value = "entityClassName") String entityClassName) {
    return metaDataService.getMetaClass(entityClassName);
  }
}

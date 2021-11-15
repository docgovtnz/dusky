package com.fronde.server.services.validation;

import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.validation.message.MessageTemplateMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {

  @Autowired
  protected ValidationService validationService;

  @RequestMapping(value = "/{entityClassName}", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public DataValidationClass getDataValidationClass(
      @PathVariable(value = "entityClassName") String entityClassName) {
    return validationService.getDataValidationClass(entityClassName);
  }

  @RequestMapping(value = "/messageTemplateMap", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public MessageTemplateMap getMessageTemplateMap() {
    return validationService.getMessageTemplateMap();
  }

}

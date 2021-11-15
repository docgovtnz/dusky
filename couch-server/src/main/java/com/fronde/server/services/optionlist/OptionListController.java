package com.fronde.server.services.optionlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/optionList")
public class OptionListController {

  @Autowired
  protected OptionListService service;

  @RequestMapping(value = "/findDisplay", method = RequestMethod.POST)
  @ResponseBody
  public List<String> findDisplayOptionsByCriteria(
      @RequestBody OptionListCriteria criteria) {
    return service.findDisplayOptionsByCriteria(criteria);
  }

}

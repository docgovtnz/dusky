package com.fronde.server.services.id;

import com.fronde.server.domain.response.PagedResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/id")
public class IdSearchController {

  @Autowired
  private IdSearchService service;

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @ResponseBody
  public PagedResponse<IdSearchDTO> search(@RequestBody IdSearchCriteria criteria) {
    return service.search(criteria);
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  public void export(IdSearchCriteria criteria, HttpServletResponse response) {
    service.export(criteria, response);
  }

  @RequestMapping(value = "/exportTransmitterList", method = RequestMethod.GET)
  public void exportTransmitterList(IdSearchCriteria criteria, HttpServletResponse response) {
    service.exportTransmitterList(criteria, response);
  }
}

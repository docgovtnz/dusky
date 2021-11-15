package com.fronde.server.services.location;

import com.fronde.server.domain.response.PagedResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/location")
public class LocationController extends LocationBaseController {

  @RequestMapping(value = "/{locationID}/currentEggs", method = RequestMethod.GET)
  @ResponseBody
  public PagedResponse<String> getCurrentEggs(@PathVariable("locationID") String locationID) {
    return service.getCurrentEggs(locationID);
  }

  @RequestMapping(value = "/{locationID}/currentChicks", method = RequestMethod.GET)
  @ResponseBody
  public PagedResponse<String> getCurrentChicks(@PathVariable("locationID") String locationID) {
    return service.getCurrentChicks(locationID);
  }

  @RequestMapping(value = "/{locationID}/nextClutchOrder", method = RequestMethod.GET)
  @ResponseBody
  public Integer getNextClutchOrder(@PathVariable("locationID") String locationID) {
    return service.getNextClutchOrder(locationID);
  }

  @RequestMapping(value = "/{locationID}/clutch", method = RequestMethod.GET)
  @ResponseBody
  public String getClutch(@PathVariable("locationID") String locationID) {
    return service.getClutch(locationID);
  }

}

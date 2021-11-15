package com.fronde.server.services.options;

import com.fronde.server.domain.OptionListEntity;
import com.fronde.server.domain.TxMortalityEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.authorization.CheckPermission;
import com.fronde.server.services.authorization.Permission;
import com.fronde.server.services.authorization.PublicAPI;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/api/options")
public class OptionsController {

  @Value("${options.cacheRefreshMillis:1200000}")
  private Long cacheRefreshMillis;

  @Autowired
  protected OptionsService optionsService;

  @RequestMapping(value = "/clientCacheRefreshPeriodInMillis", method = RequestMethod.GET)
  @ResponseBody
  @PublicAPI
  public Long getClientCacheRefreshPeriodInMillis() {
    return cacheRefreshMillis;
  }

  @RequestMapping(value = "/birdNames", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findBirdNames() {
    return optionsService.findBirdNames();
  }

  @RequestMapping(value = "/birdSummaries", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<BirdSummaryDTO> findBirdSummaries() {
    return optionsService.findBirdSummaries();
  }

  @RequestMapping(value = "/islandNames", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findIslandNames() {
    return optionsService.findIslandNames();
  }

  @RequestMapping(value = "/txIds", method = RequestMethod.GET)
  @ResponseBody
  public List<String> findTxIds() {
    return optionsService.findTxIds();
  }

  @RequestMapping(value = "/users", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findUsers() {
    return optionsService.findUsers();
  }

  @RequestMapping(value = "/locationNames", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findLocationNames() {
    return optionsService.findLocationNames();
  }

  @RequestMapping(value = "/locationSummaries", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<LocationSummaryDTO> findLocationSummaries() {
    return optionsService.findLocationSummaries();
  }

  @RequestMapping(value = "/personSummaries", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<PersonSummaryDTO> findPersonSummaries() {
    return optionsService.findPersonSummaries();
  }

  @RequestMapping(value = "/recordActivities", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findRecordActivities() {
    return optionsService.findRecordActivities();
  }

  @RequestMapping(value = "/recordTypes", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findRecordTypes() {
    return optionsService.findRecordTypes();
  }

  @RequestMapping(value = "/recordReasons", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findRecordReasons() {
    return optionsService.findRecordReasons();
  }

  @RequestMapping(value = "/spareTransmitters", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findSpareTransmitters() {
    return optionsService.findTransmitters(false);
  }

  @RequestMapping(value = "/allTransmitters", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findAllTransmitters() {
    return optionsService.findTransmitters(true);
  }

  @RequestMapping(value = "/txMortalityTypes", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findTxMortalityTypes() {
    return optionsService.getTxMortalityTypes();
  }

  @RequestMapping(value = "/otherSampleTypes", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findOtherSampleTypes() {
    return optionsService.findOtherSampleTypes();
  }

  @RequestMapping(value = "/spermDiluents", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findSpermDiluents() {
    return optionsService.findSpermDiluents();
  }

  @RequestMapping(value = "/sampleCategories", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findSampleCategories() {
    return optionsService.findSampleCategories();
  }

  @RequestMapping(value = "/sampleTypes", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findSampleTypes() {
    return optionsService.findSampleTypes();
  }

  @RequestMapping(value = "/stations", method = RequestMethod.GET)
  @ResponseBody
  @CheckPermission(Permission.AUTHENTICATED)
  public List<String> findStations() {
    return optionsService.findStations();
  }

  @RequestMapping(value = "/optionLists", method = RequestMethod.GET)
  @ResponseBody
  // get options made public so that cache gets an initial value even if the users isn't logged in
  // if we don't do this then the user either has to refresh the screen or wait for the next polling
  // cycle to get the options. Once the options are populated then from then on the cached value is
  // used until the next successful poll.
  @PublicAPI
  public Map<String, List<String>> getOptions() {
    return optionsService.getOptions();
  }


  @RequestMapping(value = "/optionListTitles", method = RequestMethod.GET)
  @ResponseBody
  // get options made public so that cache gets an initial value even if the users isn't logged in
  // if we don't do this then the user either has to refresh the screen or wait for the next polling
  // cycle to get the options. Once the options are populated then from then on the cached value is
  // used until the next successful poll.
  @PublicAPI
  public Map<String, String> getOptionListTitles() {
    return optionsService.getOptionListTitles();
  }


  @RequestMapping(value = "/optionLists", method = RequestMethod.POST)
  @ResponseBody
  @CheckPermission(Permission.TXMORTALITY_EDIT)
  public Response<OptionListEntity> save(@RequestBody OptionListEntity newList) {
    return optionsService.saveList(newList);
  }

}

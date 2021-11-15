package com.fronde.server.services.options;

import com.fronde.server.domain.OptionListEntity;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdRepository;
import com.fronde.server.services.island.IslandRepository;
import com.fronde.server.services.location.LocationRepository;
import com.fronde.server.services.noranet.NoraNetRepository;
import com.fronde.server.services.optionlist.OptionListRepository;
import com.fronde.server.services.person.PersonRepository;
import com.fronde.server.services.record.RecordRepository;
import com.fronde.server.services.sample.SampleRepository;
import com.fronde.server.services.transmitter.TransmitterRepository;
import com.fronde.server.services.transmitter.TransmitterService;
import com.fronde.server.services.txmortality.TxMortalityRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * OptionService. Get options for typeahead inputs, usually a list of strings.
 */
@Component
public class OptionsService {

  @Autowired
  protected OptionListRepository optionListRepository;

  @Autowired
  protected BirdRepository birdRepository;

  @Autowired
  protected PersonRepository personRepository;

  @Autowired
  protected LocationRepository locationRepository;

  @Autowired
  protected RecordRepository recordRepository;

  @Autowired
  protected TransmitterService transmitterService;

  @Autowired
  protected IslandRepository islandRepository;

  @Autowired
  protected TransmitterRepository transmitterRepository;

  @Autowired
  protected TxMortalityRepository txMortalityRepository;

  @Autowired
  protected SampleRepository sampleRepository;

  @Autowired
  protected NoraNetRepository noraNetRepository;

  public List<String> findBirdNames() {
    return birdRepository.findBirdNames();
  }

  public List<String> findUsers() {
    return personRepository.findUsers();
  }

  public List<String> findLocationNames() {
    return locationRepository.findLocationNames();
  }

  public List<String> findRecordActivities() {
    return recordRepository.findRecordActivities();
  }

  public List<String> findRecordTypes() {
    return recordRepository.findRecordTypes();
  }

  public List<String> findRecordReasons() {
    return recordRepository.findRecordReasons();
  }

  public List<BirdSummaryDTO> findBirdSummaries() {
    return birdRepository.findBirdSummaries(null);
  }

  public List<LocationSummaryDTO> findLocationSummaries() {
    return locationRepository.findLocationSummaries();
  }

  public List<PersonSummaryDTO> findPersonSummaries() {
    return personRepository.findPersonSummaries();
  }

  public List<String> findIslandNames() {
    return islandRepository.findIslandNames();
  }

  public List<String> findTransmitters(boolean fullList) {
    return transmitterService.findTransmitters(fullList);
  }

  public List<String> getTxMortalityTypes() {
    return txMortalityRepository.getTxMortalityTypes();
  }

  public List<String> findTxIds() {
    return transmitterRepository.findTxIds();
  }

  public List<String> findOtherSampleTypes() {
    return sampleRepository.findOtherSampleTypes();
  }

  public List<String> findSpermDiluents() {
    return sampleRepository.findSpermDiluents();
  }

  public List<String> findSampleCategories() {
    return sampleRepository.findSampleCategories();
  }

  public List<String> findSampleTypes() {
    return sampleRepository.findSampleTypes();
  }

  public List<String> findStations() { return noraNetRepository.findStations(); }

  public Map<String, List<String>> getOptions() {
    return optionListRepository.getOptions();
  }

  public Map<String, String> getOptionListTitles() {
    return optionListRepository.getOptionListTitles();
  }
  public Response<OptionListEntity> saveList(OptionListEntity newList) {
    return optionListRepository.saveList(newList);
  }
}

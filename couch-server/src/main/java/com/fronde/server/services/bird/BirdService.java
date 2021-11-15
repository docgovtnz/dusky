package com.fronde.server.services.bird;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.LifeStageEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.TransmitterEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.feedout.FeedOutService;
import com.fronde.server.services.lifestage.LifeStageService;
import com.fronde.server.services.options.BirdSummaryDTO;
import com.fronde.server.services.record.BirdTransmitterHistoryDTO;
import com.fronde.server.services.record.DatedMeasureDetailDTO;
import com.fronde.server.services.record.RecordService;
import com.fronde.server.services.transmitter.TransmitterService;
import com.fronde.server.utils.ServiceUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BirdService extends BirdBaseService {

  @Autowired
  protected FeedOutService feedOutService;
  @Autowired
  protected RecordService recordService;
  @Autowired
  protected TransmitterService transmitterService;
  @Autowired
  protected LifeStageService lifeStageService;

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    DeleteByIdCheckDTO dto = new DeleteByIdCheckDTO();
    dto.setId(docId);
    // we can't delete something that doesn't exist
    dto.setDeleteOk(docId != null && !repository.hasReferences(docId));
    return dto;
  }

  public PagedResponse<BirdSearchDTO> findSearchDTOByCriteria(BirdCriteria criteria) {
    return repository.findSearchDTOByCriteria(criteria);
  }

  @Override
  public BirdEntity findById(String docId) {
//        BirdLocationCriteria criteria = new BirdLocationCriteria();
//        criteria.setQueryDate(new Date());
//        findBirdLocations(criteria);
    return super.findById(docId);
  }

  public List findBirdLocations(BirdLocationCriteria criteria) {
    List<BirdLocationDTO> birdLocations = repository.findBirdsAliveAtDate(criteria);

    // Loop through the result set and delete the redundant rows by not copying them into the result list
    String birdID = "";
    List<BirdLocationDTO> resultList = new ArrayList<>();
    for (BirdLocationDTO nextDTO : birdLocations) {
      boolean differentID = !birdID.equals(nextDTO.getBirdID());
      if (differentID) {
        birdID = nextDTO.getBirdID();
        resultList.add(nextDTO);
      }
    }

    return resultList;
  }

  public List<BirdSummaryDTO> findBirdSummaries(BirdSummaryCriteria criteria) {
    return repository.findBirdSummaries(criteria);
  }

  public DatedMeasureDetailDTO getCurrentMeasureDetail(String birdID) {
    return recordService.getCurrentMeasureDetailByBirdID(birdID);
  }

  public List<DatedMeasureDetailDTO> getMeasureDetailHistory(String birdID) {
    return recordService.getMeasureDetailHistoryByBirdID(birdID);
  }

  @Override
  public Response<BirdEntity> save(BirdEntity entity) {
    return this.save(entity, null);
  }

  public void saveWithThrow(BirdEntity entity, String modifiedByRecordId) throws RuntimeException {
    ServiceUtils.throwIfRequired(save(entity, modifiedByRecordId), "Bird", entity.getId());
  }

  public Response<BirdEntity> save(BirdEntity entity, String modifiedByRecordId) {
    entity.setModifiedByRecordId(modifiedByRecordId);
    boolean createUnknownLifeStage = entity.getId() == null;
    Response<BirdEntity> response = super.save(entity);
    if (createUnknownLifeStage) {
      LifeStageEntity ls = new LifeStageEntity();
      ls.setAgeClass("Egg"); // TODO reference constant
      ls.setBirdID(entity.getId());
      ls.setChangeType("New Bird"); // TODO reference constant
      ls.setDateTime(new Date(Long.MIN_VALUE)); // TODO confirm
      lifeStageService.saveWithThrow(ls);
    }
    return response;
  }

  public BirdEntity findBirdIDByTransmitter(String island, int channel, String sex) {
    return repository.findBirdIDByTransmitter(island, channel, sex);
  }

  public List<BirdEntity> findBirdsByTransmitter(String island, int uhfId, String sex) {
    return repository.findBirdsByTransmitter(island, uhfId, sex);
  }


  public void export(BirdCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "House Name", "House ID", "Current Island", "Current Location", "Sex", "Alive",
        "Age Class", "Age", "Transmitter group");
    List<String> props = Arrays.asList(
        "birdName", "houseID", "currentIsland", "currentLocation", "sex", "alive",
        "ageClass", "ageInDays", "transmitterGroup");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<BirdSearchDTO> pr = this.findSearchDTOByCriteria(criteria);
    exportUtils.export(response, pr, header, props, "bird");
  }

  public CurrentBandDTO getCurrentBand(String birdID) {
    RecordEntity r = recordService.findLatestBandsRecordByBirdID(birdID);
    if (r != null) {
      CurrentBandDTO dto = new CurrentBandDTO();
      dto.setDateTime(r.getDateTime());
      dto.setLeg(r.getBands().getLeg());
      dto.setNewBandNumber(r.getBands().getNewBandNumber());
      return dto;
    } else {
      return null;
    }
  }

  public CurrentChipDTO getCurrentChip(String birdID) {
    RecordEntity r = recordService.findLatestChipsRecordByBirdID(birdID);
    if (r != null) {
      CurrentChipDTO dto = new CurrentChipDTO();
      dto.setDateTime(r.getDateTime());
      dto.setMicrochip(r.getChips().getMicrochip());
      return dto;
    } else {
      return null;
    }
  }

  public TransmitterEntity getCurrentTransmitter(String birdID) {
    RecordEntity r = recordService.findLatestTransmitterChangeRecordByBirdID(birdID);
    if (r != null && r.getTransmitterChange().getTxTo() != null) {
      return transmitterService.findById(r.getTransmitterChange().getTxTo());
    } else {
      return null;
    }
  }

  public Date getCurrentTransmitterDeployedDate(String birdID) {
    RecordEntity r = recordService.findLatestTransmitterChangeRecordByBirdID(birdID);
    if (r != null && r.getTransmitterChange().getTxTo() != null) {
      return r.getDateTime();
    } else {
      return null;
    }
  }

  public Date getCurrentTransmitterExpiryDate(String birdID) {
    RecordEntity r = recordService.findLatestTransmitterChangeRecordByBirdID(birdID);
    if (r != null && r.getTransmitterChange().getTxTo() != null) {
      return transmitterService.getExpiryDate(r.getTransmitterChange().getTxTo());
    } else {
      return null;
    }
  }

  public List<BirdTransmitterHistoryDTO> getTransmitterHistory(String birdID) {
    return recordService.findBirdTransmitterHistoryDTOByBirdID(birdID);
  }

  public String getAgeClass(String birdID) {
    LifeStageEntity ls = repository.findLatestLifeStageByBirdID(birdID);
    if (ls != null) {
      return ls.getAgeClass();
    } else {
      return null;
    }
  }

  public String getMilestone(String birdID) {
    BirdEntity b = repository.findById(birdID).get();
    // milestone is all based on dates
    String ageClass = getAgeClass(birdID);
    if ("Adult".equals(ageClass) || b.getDateIndependent() != null) {
      return "Independent";
    } else if ("Juvenile".equals(ageClass)) {
      return "Dependent";
    } else if (b.getDateWeaned() != null) {
      return "Weaned";
    } else if (b.getDateFledged() != null) {
      return "Fledged";
    } else if (b.getDateHatched() != null) {
      return "Hatched";
    } else if (b.getDateLaid() != null) {
      return "Laid";
    }
    return "Unknown";
  }

  public String getMortality(String birdID) {
    BirdEntity b = findById(birdID);
    if (b != null) {
      String ageClass = getAgeClass(birdID);
      // dead is always dead
      if (b.getAlive() != null && !b.getAlive()) {
        return "Dead";
      } else if ("Egg".equals(ageClass)) {
        if ("infert".equals(b.getViable())) {
          return "Infertile";
        } else if ("fert".equals((b.getViable()))) {
          return "Fertile";
        } else {
          return "Unknown";
        }
      } else if (b.getAlive() != null && b.getAlive()) {
        return "Alive";
      } else {
        return null;
      }
    }
    return null;
  }

  public Integer getAgeInDays(String birdID) {
    BirdEntity b = findById(birdID);
    if (b != null) {
      return BirdAgeUtils
          .calculateAgeInDays(b.getDateLaid(), b.getDateHatched(), b.getDateFirstFound(),
              b.getEstAgeWhen1stFound(), b.getAlive(), b.getViable(), b.getDemise());
    } else {
      return null;
    }
  }

  public String getName(String birdID) {
    return repository.getName(birdID);
  }

  /**
   * Returns true if the specified bird name is unique for the specified bird (via birdID). birdID
   * can be null in the case of this being a new bird.
   *
   * @param birdID
   * @param birdName
   * @return
   */
  public boolean isUniqueName(String birdID, String birdName) {
    boolean exists = repository.existsByNameExcluding(birdName, birdID);
    return !exists;
  }

  /**
   * Returns true if the specified house ID is unique for the specified bird (via birdID). birdID
   * can be null in the case of this being a new bird.
   *
   * @param birdID
   * @param houseID
   * @return
   */
  public boolean isUniqueHouseID(String birdID, String houseID) {
    boolean exists = repository.existsByHouseIDExcluding(houseID, birdID);
    return !exists;
  }

  public List<LifeStageDTO> getLifeStages(String[] birdIDs) {
    List<LifeStageDTO> results = new LinkedList<>();
    for (String birdID : birdIDs) {
      results.add(getLifeStage(birdID));
    }
    return results;
  }

  public LifeStageDTO getLifeStage(String birdID) {
    LifeStageDTO result = new LifeStageDTO();
    result.setBirdID(birdID);

    LifeStageEntity ls = repository.findLatestLifeStageByBirdID(birdID);
    if (ls != null) {
      result.setAgeClass(ls.getAgeClass());
    }

    BirdEntity b = repository.findById(birdID).get();
    // milestone is all based on dates
    String ageClass = result.getAgeClass();
    if ("Adult".equals(ageClass) || b.getDateIndependent() != null) {
      result.setMilestone("Independent");
    } else if ("Juvenile".equals(ageClass)) {
      result.setMilestone("Dependent");
    } else if (b.getDateWeaned() != null) {
      result.setMilestone("Weaned");
    } else if (b.getDateFledged() != null) {
      result.setMilestone("Fledged");
    } else if (b.getDateHatched() != null) {
      result.setMilestone("Hatched");
    } else if (b.getDateLaid() != null) {
      result.setMilestone("Laid");
    } else {
      result.setMilestone("Unknown");
    }
    if (b != null) {
      // dead is always dead
      if (b.getAlive() != null && !b.getAlive()) {
        result.setMortality("Dead");
      } else if ("Egg".equals(ageClass)) {
        if ("infert".equals(b.getViable())) {
          result.setMortality("Infertile");
        } else if ("fert".equals((b.getViable()))) {
          result.setMortality("Fertile");
        } else {
          result.setMortality("Unknown");
        }
      } else if (b.getAlive() != null && b.getAlive()) {
        result.setMortality("Alive");
      }
    }
    return result;
  }


}

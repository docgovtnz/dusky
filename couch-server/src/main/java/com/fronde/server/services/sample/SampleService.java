package com.fronde.server.services.sample;

import com.fronde.server.domain.BloodSampleDetailEntity;
import com.fronde.server.domain.BloodSampleEntity;
import com.fronde.server.domain.OtherSampleEntity;
import com.fronde.server.domain.RecordEntity;
import com.fronde.server.domain.SampleEntity;
import com.fronde.server.domain.SpermSampleEntity;
import com.fronde.server.domain.SwabSampleEntity;
import com.fronde.server.domain.response.DeleteByIdCheckDTO;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.domain.response.Response;
import com.fronde.server.services.bird.BirdCriteria;
import com.fronde.server.services.bird.BirdSearchDTO;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.record.RecordService;
import com.fronde.server.utils.ServiceUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SampleService extends SampleBaseService {

  @Autowired
  private RecordService recordService;

  @Autowired
  private BirdService birdService;

  @Override
  public SampleEntity findById(String docId) {
    SampleEntity sampleEntity = super.findById(docId);
    if (sampleEntity == null) {
      return null;
    }
    if (sampleEntity.getHaematologyTestList() != null) {
      // sort the tests so that they are in order of test name
      Collections.sort(sampleEntity.getHaematologyTestList(),
          (o1, o2) -> o1.getTest() == null && o2.getTest() == null ? 0
              : (o1.getTest() == null ? Integer.MIN_VALUE
                  : (o2.getTest() == null ? Integer.MAX_VALUE
                      : o1.getTest().compareToIgnoreCase(o2.getTest()))));
    }
    if (sampleEntity.getChemistryAssayList() != null) {
      // sort the tests so that they are in order of test name
      Collections.sort(sampleEntity.getChemistryAssayList(),
          (o1, o2) -> o1.getChemistryAssay() == null && o2.getChemistryAssay() == null ? 0
              : (o1.getChemistryAssay() == null ? Integer.MIN_VALUE
                  : (o2.getChemistryAssay() == null ? Integer.MAX_VALUE
                      : o1.getChemistryAssay().compareToIgnoreCase(o2.getChemistryAssay()))));
    }
    if (sampleEntity.getMicrobiologyAndParasitologyTestList() != null) {
      // sort the tests so that they are in order of test name
      Collections.sort(sampleEntity.getMicrobiologyAndParasitologyTestList(),
          (o1, o2) -> o1.getTest() == null && o2.getTest() == null ? 0
              : (o1.getTest() == null ? Integer.MIN_VALUE
                  : (o2.getTest() == null ? Integer.MAX_VALUE
                      : o1.getTest().compareToIgnoreCase(o2.getTest()))));
    }
    return sampleEntity;
  }

  @Override
  public DeleteByIdCheckDTO deleteByIdCheck(String docId) {
    // load the sample and check if it has any results
    SampleEntity s = findById(docId);
    boolean deleteOk = true;
    // if there are results then don't allow delete
    if (deleteOk && s.getHaematologyTestList() != null && !s.getHaematologyTestList().isEmpty()) {
      deleteOk = false;
    }
    if (deleteOk && s.getChemistryAssayList() != null && !s.getChemistryAssayList().isEmpty()) {
      deleteOk = false;
    }
    if (deleteOk && s.getMicrobiologyAndParasitologyTestList() != null &&
        !s.getMicrobiologyAndParasitologyTestList().isEmpty()) {
      deleteOk = false;
    }
    if (deleteOk && s.getSpermMeasureList() != null && !s.getSpermMeasureList().isEmpty()) {
      deleteOk = false;
    }
    DeleteByIdCheckDTO result = new DeleteByIdCheckDTO();
    result.setId(docId);
    result.setDeleteOk(deleteOk);
    return result;
  }

  @Override
  public void deleteById(String id) {
    preSampleDelete(id);
    super.deleteById(id);
  }

  public void preSampleDelete(String id) {
    // find associated record
    RecordEntity r = recordService.findRecordBySampleID(id);
    if (r != null) {
      // iterate through different samples of different categories until we find the detail to remove from the record
      // naively we continue even though we may have already found and removed it.
      if (r.getSwabSampleList() != null) {
        for (Iterator<SwabSampleEntity> i = r.getSwabSampleList().iterator(); i.hasNext(); ) {
          if (ObjectUtils.nullSafeEquals(i.next().getSampleID(), id)) {
            i.remove();
          }
        }
      }
      if (r.getOtherSampleList() != null) {
        for (Iterator<OtherSampleEntity> i = r.getOtherSampleList().iterator(); i.hasNext(); ) {
          if (ObjectUtils.nullSafeEquals(i.next().getSampleID(), id)) {
            i.remove();
          }
        }
      }
      List<BloodSampleEntity> bloodSampleList = r.getBloodSampleDetail().getBloodSampleList();
      if (bloodSampleList != null) {
        bloodSampleList.removeIf(
            bloodSampleEntity -> ObjectUtils.nullSafeEquals(bloodSampleEntity.getSampleID(), id));
      }
      if (r.getSpermSampleList() != null) {
        for (Iterator<SpermSampleEntity> i = r.getSpermSampleList().iterator(); i.hasNext(); ) {
          if (ObjectUtils.nullSafeEquals(i.next().getSampleID(), id)) {
            i.remove();
          }
        }
      }
      recordService.saveWithThrow(r, false);
    }
  }

  @Override
  public Response<SampleEntity> save(SampleEntity entity) {
    return save(entity, true);
  }

  public void saveWithThrow(SampleEntity entity, boolean withRecordUpdate) {
    ServiceUtils.throwIfRequired(save(entity, withRecordUpdate), "Sample", entity.getId());
  }

  public Response<SampleEntity> save(SampleEntity entity, boolean withRecordUpdate) {
    // align the sample type to the swab site for swab samples
    // this is to allow search to operate off a single field for all samples
    if ("Blood".equals(entity.getSampleCategory())) {
      entity.setSampleType(entity.getBloodDetail().getType());
    } else if ("Swab".equals(entity.getSampleCategory())) {
      entity.setSampleType(entity.getSwabDetail().getSwabSite());
    } else if ("Other".equals(entity.getSampleCategory())) {
      entity.setSampleType(entity.getOtherDetail().getType());
    } else if ("Sperm".equals(entity.getSampleCategory())) {
      entity.setSampleType("Sperm");
    }
    Response r = super.save(entity);
    if (withRecordUpdate) {
      postSampleSave(entity);
    }
    return r;
  }

  private void postSampleSave(SampleEntity entity) {
    // look up the associated record
    RecordEntity r = recordService.findRecordBySampleID(entity.getId());
    if (r != null) {
      BloodSampleDetailEntity bloodSampleDetail = r.getBloodSampleDetail();
      if (bloodSampleDetail != null && !bloodSampleDetail.getBloodSampleList().isEmpty()) {
        bloodSampleDetail.getBloodSampleList().forEach(s -> {
          if (ObjectUtils.nullSafeEquals(s.getSampleID(), entity.getId())) {
            // set sample level fields
            s.setStorageMedium(entity.getStorageMedium());
            s.setContainer(entity.getContainer());
            s.setStorageConditions(entity.getStorageConditions());
            s.setSampleTakenBy(entity.getSampleTakenBy());
            s.setSampleName(entity.getSampleName());
            // set blood specific fields
            s.setType(entity.getBloodDetail().getType());
            s.setVolumeInMl(entity.getBloodDetail().getVolumeInMl());
            s.setReasonForSample(entity.getReasonForSample());
          }
        });
      }
      if (r.getSwabSampleList() != null) {
        for (Iterator<SwabSampleEntity> i = r.getSwabSampleList().iterator(); i.hasNext(); ) {
          SwabSampleEntity s = i.next();
          if (ObjectUtils.nullSafeEquals(s.getSampleID(), entity.getId())) {
            // set sample level fields
            s.setStorageMedium(entity.getStorageMedium());
            s.setContainer(entity.getContainer());
            s.setStorageConditions(entity.getStorageConditions());
            s.setSampleTakenBy(entity.getSampleTakenBy());
            s.setSampleName(entity.getSampleName());
            // set swab specific fields
            s.setSwabSite(entity.getSwabDetail().getSwabSite());
            s.setQuantity(entity.getSwabDetail().getQuantity());
            s.setReasonForSample(entity.getReasonForSample());
          }
        }
      }
      if (r.getOtherSampleList() != null) {
        for (Iterator<OtherSampleEntity> i = r.getOtherSampleList().iterator(); i.hasNext(); ) {
          OtherSampleEntity s = i.next();
          if (ObjectUtils.nullSafeEquals(s.getSampleID(), entity.getId())) {
            // set sample level fields
            s.setStorageMedium(entity.getStorageMedium());
            s.setContainer(entity.getContainer());
            s.setStorageConditions(entity.getStorageConditions());
            s.setSampleTakenBy(entity.getSampleTakenBy());
            s.setSampleName(entity.getSampleName());
            // set other specific fields
            s.setAmount(entity.getOtherDetail().getAmount());
            s.setType(entity.getOtherDetail().getType());
            s.setUnits(entity.getOtherDetail().getUnits());
            s.setReasonForSample(entity.getReasonForSample());
          }
        }
      }
      if (r.getSpermSampleList() != null) {
        for (Iterator<SpermSampleEntity> i = r.getSpermSampleList().iterator(); i.hasNext(); ) {
          SpermSampleEntity s = i.next();
          if (ObjectUtils.nullSafeEquals(s.getSampleID(), entity.getId())) {
            // set sample level fields
            s.setContainer(entity.getContainer());
            s.setStorageConditions(entity.getStorageConditions());
            s.setSampleTakenBy(entity.getSampleTakenBy());
            s.setSampleName(entity.getSampleName());
            // set sperm specific fields
            s.setDiluent(entity.getSpermDetail().getDiluent());
            s.setVolumeInMicroL(entity.getSpermDetail().getVolumeInMicroL());
            s.setCollectionMethod(entity.getSpermDetail().getCollectionMethod());
            s.setPapillaSwelling(entity.getSpermDetail().getPapillaSwelling());
            s.setStimulation(entity.getSpermDetail().getStimulation());
            s.setStress(entity.getSpermDetail().getStress());
            s.setReasonForSample(entity.getReasonForSample());
          }
        }
      }
      recordService.saveWithThrow(r, false);
    }
  }

  public PagedResponse<SampleSearchDTO> findSearchDTOByCriteria(SampleCriteria criteria) {

    if (criteria.getAgeClass() != null) {
      BirdCriteria birdCriteria = new BirdCriteria();
      birdCriteria.setShowAlive(true);
      birdCriteria.setShowDead(true);
      birdCriteria.setAgeClass(criteria.getAgeClass());
      birdCriteria.setPageSize(9999);
      PagedResponse<BirdSearchDTO> birdSearchResults = birdService
          .findSearchDTOByCriteria(birdCriteria);
      if (birdSearchResults != null && birdSearchResults.getResults() != null) {
        if (criteria.getBirdIDs() == null) {
          criteria.setBirdIDs(new ArrayList<>());
        }

        birdSearchResults.getResults().forEach(birdSearchDTO -> {
          criteria.getBirdIDs().add(birdSearchDTO.getBirdID());
        });
      }
    }

    return repository.findSearchDTOByCriteria(criteria);
  }

  public List<TestStatsDTO> getHaematologyTestsStats(String type) {
    return repository.getHaematologyTestsStats(type);
  }

  public List<ResultRankDTO> getHaematologyTestsRanks(String type, List<ResultDTO> results) {
    return repository.getHaematologyTestsRanks(type, results);
  }

  public List<TestStatsDTO> getChemistryAssaysStats(String type) {
    return repository.getChemistryAssaysStats(type);
  }

  public List<ResultRankDTO> getChemistryAssaysRanks(String type, List<ResultDTO> results) {
    return repository.getChemistryAssaysRanks(type, results);
  }

  /**
   * Overriding method because super class can't call the DTO search method
   * "findSearchDTOByCriteria()" yet. This could evolve to something better, but this was the
   * quickest simplest solution at the time.
   *
   * @param criteria
   * @param response
   */
  @Override
  public void export(SampleCriteria criteria, HttpServletResponse response) {
    List<String> header = Arrays.asList(
        "Sample ID", "Bird", "Sample Category", "Sample Type", "Container", "Taken By",
        "Collection Date", "Island", "Location", "Haem", "Chem", "M&P", "Sperm");
    List<String> props = Arrays.asList(
        "sampleName", "birdName", "sampleCategory", "sampleType", "container",
        "sampleTakenByName", "collectionDate", "collectionIsland", "collectionLocationName",
        "hasHaematologyTests", "hasChemistryAssays", "hasMicrobiologyAndParasitologyTests",
        "hasSpermMeasures");
    criteria.setPageSize(exportUtils.getMaxRows());
    criteria.setPageNumber(1);
    PagedResponse<SampleSearchDTO> pr = this.findSearchDTOByCriteria(criteria);
    exportUtils.export(response, pr, header, props, "sample");
  }

}

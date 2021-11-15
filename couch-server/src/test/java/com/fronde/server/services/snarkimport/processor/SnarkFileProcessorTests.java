package com.fronde.server.services.snarkimport.processor;

import static org.junit.Assert.assertEquals;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.SnarkRecordEntity;
import com.fronde.server.services.snarkimport.MysteryWeightDTO;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class SnarkFileProcessorTests {

  private static final Logger logger = LoggerFactory.getLogger(SnarkFileProcessorTests.class);
  private static final String basePath =
      "/" + SnarkFileProcessorTests.class.getPackageName().replace(".", "/");

  @Test
  public void test() throws IOException {
    SnarkFileProcessorFactory processorFactory = new SnarkFileProcessorFactory();
    SnarkFileProcessor processor = processorFactory.createProcessor(new BirdIdConverter() {
      @Override
      public List<BirdEntity> convertUhfId(String island, int uhfId) {
        BirdEntity b = new BirdEntity();
        b.setId("converted_id_for_" + uhfId);
        return Arrays.asList(b);
      }
    });

    for (Resource testInputFile : getTestInputFiles()) {
      try (InputStream stream = testInputFile.getInputStream()) {
        logger.info("Testing input file '" + testInputFile.getFilename() + "'");
        assertEqualsExpectedOutput(testInputFile,
            processor.processFile("", stream, false));
      }
    }
  }

  private List<Resource> getTestInputFiles() throws IOException {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    return Arrays.asList(resolver.getResources("classpath:" + basePath + "/input/*.snark"));
  }

  private static void assertEqualsExpectedOutput(Resource testInputFile,
      SnarkFileProcessResult result) throws IOException {
    assertEquals(getExpectedOutput(testInputFile).replace("\r\n", "\n"),
        toString(result).replace("\r\n", "\n"));
  }

  private static String toString(SnarkFileProcessResult result) {
    String string = "";
    string += "Snark Records\n";
    Map<Date, List<SnarkRecordEntity>> recordsByEvening = result.getSnarkRecordsByEvening();
    List<Date> evenings = new ArrayList<>();
    evenings.addAll(recordsByEvening.keySet());
    Collections.sort(evenings);
    for (Date evening : evenings) {
      string +=
          "evening=" + evening.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
      List<SnarkRecordEntity> records = recordsByEvening.get(evening);
      for (SnarkRecordEntity record : records) {
        string += toString(record) + "\n";
      }
    }
    string += "Mystery Weights\n";
    for (MysteryWeightDTO mw : result.getMysteryWeightList()) {
      string += mw.toString() + "\n";
    }
    return string;
  }

  private static String toString(SnarkRecordEntity record) {
    return "SnarkRecordEntity{" +
        "arriveDateTime=" + record.getArriveDateTime().toInstant().atZone(ZoneId.systemDefault())
        .toLocalDateTime() +
        ", birdCert='" + record.getBirdCert() + '\'' +
        ", birdID='" + record.getBirdID() + '\'' +
        ", departDateTime=" + (record.getDepartDateTime() == null ? ""
        : record.getDepartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) +
        ", mating=" + record.getMating() +
        ", weight=" + record.getWeight() +
        '}';
  }

  private static String getExpectedOutput(Resource testInputFile) throws IOException {
    // derive the expected output resource form the input
    String expectedOutputFilename = testInputFile.getFilename().replace(".snark", ".txt");
    String expectedOutputPath =
        "classpath:" + basePath + "/expectedoutput/" + expectedOutputFilename;
    Resource resource = new DefaultResourceLoader().getResource(expectedOutputPath);
    if (resource.exists()) {
      return IOUtils.toString(resource.getInputStream());
    } else {
      return "File '" + expectedOutputFilename + "' not found";
    }
  }
}

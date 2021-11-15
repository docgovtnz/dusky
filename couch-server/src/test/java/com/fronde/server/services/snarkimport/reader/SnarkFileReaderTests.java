package com.fronde.server.services.snarkimport.reader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class SnarkFileReaderTests {

  private static final Logger logger = LoggerFactory.getLogger(SnarkFileReaderTests.class);
  private static final String basePath =
      "/" + SnarkFileReaderTests.class.getPackageName().replace(".", "/");

  @Test
  public void test() throws IOException {
    for (Resource testInputFile : getTestInputFiles()) {
      try (SnarkFileReader reader = new SnarkFileReader(2000, testInputFile.getInputStream())) {
        logger.info("Testing input file '" + testInputFile.getFilename() + "'");
        assertEqualsExpectedOutput(testInputFile, reader.readData());
      }
    }
  }

  private List<Resource> getTestInputFiles() throws IOException {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    return Arrays.asList(resolver.getResources("classpath:" + basePath + "/input/*.snark"));
  }

  private static void assertEqualsExpectedOutput(Resource testInputFile,
      List<SnarkFileRecord> records) throws IOException {
    assertEquals(getExpectedOutput(testInputFile).replace("\r\n", "\n"),
        toString(records).replace("\r\n", "\n"));
  }

  private static String toString(List<SnarkFileRecord> records) {
    String string = "";
    for (SnarkFileRecord record : records) {
      string += record.toString() + "\n";
    }
    return string;
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

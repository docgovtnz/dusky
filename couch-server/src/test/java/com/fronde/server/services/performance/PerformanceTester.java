package com.fronde.server.services.performance;

import com.fronde.server.services.authentication.AuthenticationRequest;
import com.fronde.server.services.authentication.AuthenticationResponse;
import com.fronde.server.services.performance.tests.BirdSaveAndFindTest;
import com.fronde.server.utils.JsonUtils;
import com.fronde.server.utils.SSLUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * A simple performance tester framework for measuring response times of the application against the
 * REST API
 */
public class PerformanceTester {

  protected PerformanceTest[] performanceTests;

  protected TestContext context;

  protected JsonUtils jsonUtils = new JsonUtils();

  public PerformanceTester() {
    this.context = new TestContext();
    this.context.setBaseURL("https://localhost:8443");
    this.context.setJsonUtils(jsonUtils);

    performanceTests = new PerformanceTest[]{
        //new BirdSearchTest(),
        //new RecordSearchTest(),
        new BirdSaveAndFindTest(),
    };

    authenticate();
  }

  private void authenticate() {

    AuthenticationRequest authenticationRequest = new AuthenticationRequest();
    authenticationRequest.setUsername("<username>");
    authenticationRequest.setPassword("<password>");

    String url = context.getBaseURL() + "/api/authenticate";
    String body = jsonUtils.toJson(authenticationRequest);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    HttpEntity<String> request = new HttpEntity<>(body, headers);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<AuthenticationResponse> response = restTemplate
        .exchange(url, HttpMethod.POST, request, AuthenticationResponse.class);
    AuthenticationResponse authenticationResponse = response.getBody();

    this.context.setJwtToken(authenticationResponse.getToken());
  }

  public void executeBenchmark() {
    for (PerformanceTest performanceTest : performanceTests) {
      List<TestResult> results = executeTypeOfTest(performanceTest);
      analyseResults(performanceTest.getClass().getSimpleName(), results);
    }
  }

  private List<TestResult> executeTypeOfTest(PerformanceTest performanceTest) {
    List<TestResult> results = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      TestResult testResult = executeTest(performanceTest);
      results.add(testResult);
    }
    return results;
  }

  private TestResult executeTest(PerformanceTest performanceTest) {
    TestResult testResult = new TestResult();
    testResult.setName(performanceTest.getClass().getSimpleName());

    long startTime = System.currentTimeMillis();

    performanceTest.executeTest(context);

    long duration = System.currentTimeMillis() - startTime;
    testResult.setDuration(duration);

    System.out.println(testResult.getName() + ": " + testResult.getDuration());
    return testResult;
  }

  private void analyseResults(String testName, List<TestResult> results) {

    results.sort((o1, o2) -> {
      Long l1 = o1.getDuration();
      Long l2 = o2.getDuration();
      return l1.compareTo(l2);
    });

    long min = results.get(0).getDuration();
    long max = results.get(results.size() - 1).getDuration();
    int percentileIdx = Math.round(results.size() * 0.90f);
    long highPercentile = results.get(percentileIdx).getDuration();

    long sum = 0;
    for (TestResult tr : results) {
      sum += tr.getDuration();
    }

    long mean = Math.round((float) sum / results.size());

    System.out.println(testName + ": " + min + ", " + mean + ", " + highPercentile + ", " + max);

  }

  public static void main(String[] args) throws Exception {
    SSLUtils.trustAllCertificates();

    PerformanceTester performanceTester = new PerformanceTester();
    performanceTester.executeBenchmark();
  }
}

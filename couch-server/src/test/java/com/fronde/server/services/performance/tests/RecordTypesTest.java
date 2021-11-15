package com.fronde.server.services.performance.tests;

import com.fronde.server.services.performance.PerformanceTest;
import com.fronde.server.services.performance.TestContext;
import java.util.Calendar;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RecordTypesTest implements PerformanceTest {


  @Override
  public void executeTest(TestContext context) {
    RestTemplate restTemplate = new RestTemplate();

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + context.getJwtToken());
    headers.add("Content-Type", "application/json");

    String url = context.getBaseURL() + "/api/options/recordTypes";
    String body = "";

    HttpEntity<String> request = new HttpEntity<>(body, headers);
    ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
    List listResponse = response.getBody();
    System.out.println("Record Types results = " + listResponse.size());
  }
}

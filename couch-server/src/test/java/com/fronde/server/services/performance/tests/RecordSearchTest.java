package com.fronde.server.services.performance.tests;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.performance.PerformanceTest;
import com.fronde.server.services.performance.TestContext;
import com.fronde.server.services.record.RecordCriteria;
import java.util.Calendar;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RecordSearchTest implements PerformanceTest {


  @Override
  public void executeTest(TestContext context) {
    RestTemplate restTemplate = new RestTemplate();

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);

    RecordCriteria criteria = new RecordCriteria();
    criteria.setFromDate(calendar.getTime());
    criteria.setPageSize(20);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + context.getJwtToken());
    headers.add("Content-Type", "application/json");

    String url = context.getBaseURL() + "/api/record/searchDTO";
    String body = context.getJsonUtils().toJson(criteria);

    HttpEntity<String> request = new HttpEntity<>(body, headers);
    ResponseEntity<PagedResponse> response = restTemplate
        .exchange(url, HttpMethod.POST, request, PagedResponse.class);
    PagedResponse pagedResponse = response.getBody();
    System.out.println("Record results = " + pagedResponse.getResults().size());
  }
}

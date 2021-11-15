package com.fronde.server.services.performance.tests;

import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.bird.BirdCriteria;
import com.fronde.server.services.performance.PerformanceTest;
import com.fronde.server.services.performance.TestContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BirdSearchTest implements PerformanceTest {

  @Override
  public void executeTest(TestContext context) {
    RestTemplate restTemplate = new RestTemplate();

    BirdCriteria birdCriteria = new BirdCriteria();
    birdCriteria.setShowAlive(true);
    birdCriteria.setPageSize(20);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + context.getJwtToken());
    headers.add("Content-Type", "application/json");

    String url = context.getBaseURL() + "/api/bird/searchDTO";
    String body = context.getJsonUtils().toJson(birdCriteria);

    HttpEntity<String> request = new HttpEntity<>(body, headers);
    ResponseEntity<PagedResponse> response = restTemplate
        .exchange(url, HttpMethod.POST, request, PagedResponse.class);
    PagedResponse pagedResponse = response.getBody();

  }
}

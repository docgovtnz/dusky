package com.fronde.server.services.performance.tests;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.domain.response.PagedResponse;
import com.fronde.server.services.bird.BirdCriteria;
import com.fronde.server.services.performance.PerformanceTest;
import com.fronde.server.services.performance.TestContext;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BirdSaveAndFindTest implements PerformanceTest {

  private static int ix = 0;

  @Override
  public void executeTest(TestContext context) {
    ix++;

    RestTemplate restTemplate = new RestTemplate();

    BirdCriteria birdCriteria = new BirdCriteria();
    birdCriteria.setShowAlive(true);
    birdCriteria.setPageSize(20);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + context.getJwtToken());
    headers.add("Content-Type", "application/json");

    String url = context.getBaseURL() + "/api/bird/searchDTO";

    String name = "BB2-" + ix;

    BirdEntity birdEntity = new BirdEntity();
    birdEntity.setBirdName(name);
    String body = context.getJsonUtils().toJson(birdEntity);

    HttpEntity<String> request = new HttpEntity<>(body, headers);
    ResponseEntity<PagedResponse> response = restTemplate
        .exchange("http://localhost:4200/api/bird/", HttpMethod.POST, request, PagedResponse.class);
    PagedResponse pagedResponse = response.getBody();

    HttpEntity<String> request2 = new HttpEntity<>(null, headers);
    ResponseEntity<List> response2 = restTemplate
        .exchange("http://localhost:4200/api/options/birdSummaries", HttpMethod.GET, request2,
            List.class);

    List<LinkedHashMap> res = (List<LinkedHashMap>) response2.getBody();
    List<LinkedHashMap> finalResult = res.stream().filter(b -> b.get("birdName").equals(name))
        .collect(Collectors.toList());
    System.err.println(name + " - " + (finalResult.size() == 1 ? "Success" : "Failed"));
  }
}

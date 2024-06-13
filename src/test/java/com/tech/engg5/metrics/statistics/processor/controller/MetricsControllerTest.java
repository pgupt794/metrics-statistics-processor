package com.tech.engg5.metrics.statistics.processor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.engg5.metrics.statistics.processor.Fixture;
import com.tech.engg5.metrics.statistics.processor.IntegrationTestBase;
import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.lang.Boolean.TRUE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class MetricsControllerTest extends IntegrationTestBase {

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  private ReactiveMongoOperations reactiveMongoOperations;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

  private static final CustomComparator COMPARATOR = new CustomComparator(JSONCompareMode.LENIENT,
    new Customization(Metrics.Fields.lastUpdatedTs, ((o1, o2) -> TRUE)));

  @BeforeEach
  @AfterEach
  void setUp() {
    reactiveMongoOperations.remove(Metrics.class).all().block();
  }

  @Test
  @SneakyThrows
  @DisplayName("Verify that response is Success and status code 200 when metrics request is received.")
  void shouldReturn2XXResponseOnMetricsRequest() {
    val request = OBJECT_MAPPER.readValue(Fixture.CONTROLLER_REQUEST
      .loadFixture("metrics-success-request.json", Fixture.SubPath.METRICS), Metrics.class);
    val dbEntry = OBJECT_MAPPER.readValue(Fixture.DATABASE.loadFixture("metrics-success-database.json",
      Fixture.SubPath.METRICS), Metrics.class);

    var response = webTestClient
      .post()
      .uri("/event/metrics")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(request)
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(String.class)
      .returnResult().getResponseBody();

    thenExpectDatabaseEntriesMetrics(Metrics.class, dbEntry);

    assert response != null;
    assert response.equals("Saved Success.");
  }
}

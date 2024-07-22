package com.tech.engg5.metrics.statistics.processor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.engg5.metrics.statistics.processor.Fixture;
import com.tech.engg5.metrics.statistics.processor.IntegrationTestBase;
import com.tech.engg5.metrics.statistics.processor.metrics.enums.BatchType;
import com.tech.engg5.metrics.statistics.processor.metrics.enums.ComponentName;
import com.tech.engg5.metrics.statistics.processor.metrics.enums.ComponentStatus;
import com.tech.engg5.metrics.statistics.processor.metrics.model.Component;
import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import com.tech.engg5.metrics.statistics.processor.metrics.service.MetricsService;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class MetricsServiceTest extends IntegrationTestBase {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ReactiveMongoOperations reactiveMongoOperations;

  @Autowired
  MetricsService metricsService;

  @BeforeEach
  @AfterEach
  void setUp() {
    reactiveMongoOperations.remove(Metrics.class).all().block();
  }

  @Test
  @SneakyThrows
  @DisplayName("Verify that metrics object get saved in Database.")
  void shouldSaveMetricsInDB() {
    val dbEntry = objectMapper.readValue(Fixture.DATABASE.loadFixture("metrics-success-database.json",
      Fixture.SubPath.METRICS), Metrics.class);

    val metrics = Metrics.builder()
      .correlationId("4d188151-f900-45cc-b40b-39b1bac11ced")
      .batchId("8eba613a-2670-443c-b53e-4aea50a85ac6")
      .batchType(BatchType.CSV_BATCH)
      .mappingId("1FI_HARRY_POTTER_AND_THE_SORCERER_STONE")
      .component(Component.builder()
        .name(ComponentName.CSV_BATCH_PROCESSOR)
        .status(ComponentStatus.PROCESSING_COMPLETED)
        .build())
      .createdTs(Instant.now())
      .lastUpdatedTs(Instant.now())
      .build();

    val status = metricsService.saveMetricsRequest(metrics).block();
    thenExpectDatabaseEntriesMetrics(Metrics.class, dbEntry);
  }
}

package com.tech.engg5.metrics.statistics.processor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.engg5.metrics.statistics.processor.Fixture;
import com.tech.engg5.metrics.statistics.processor.IntegrationTestBase;
import com.tech.engg5.metrics.statistics.processor.metrics.enums.BatchType;
import com.tech.engg5.metrics.statistics.processor.statistics.model.mongo.Statistics;
import com.tech.engg5.metrics.statistics.processor.statistics.service.StatisticsService;
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsServiceTest extends IntegrationTestBase {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ReactiveMongoOperations reactiveMongoOperations;

  @Autowired
  StatisticsService statisticsService;

  @BeforeEach
  @AfterEach
  void setUp() {
    reactiveMongoOperations.remove(Statistics.class).all().block();
  }

  @Test
  @SneakyThrows
  @DisplayName("Verify that statistics object get saved in Database.")
  void shouldSaveStatisticsInDB() {
    val batchStatisticsFile = "batch-statistics-success.json";
    val dbEntry = objectMapper.readValue(Fixture.DATABASE.loadFixture(batchStatisticsFile,
      Fixture.SubPath.STATISTICS), Statistics.class);

    val batchDate = Instant.parse("2024-07-17T07:38:49Z");

    Statistics statistics = Statistics.builder()
      .batchId("8966b472-1eb5-4750-aba9-811f33056fc0")
      .batchType(BatchType.CSV_BATCH.name())
      .fileName("CSV_BATCH_17072024_171759.csv")
      .eventReceived(50L)
      .eventFailed(14L)
      .eventSuccess(36L)
      .batchDate(batchDate)
      .build();

    val invoke = statisticsService.saveBatchStatistics(statistics).block();
    thenExpectDatabaseEntriesStatistics(Statistics.class, dbEntry);
  }
}

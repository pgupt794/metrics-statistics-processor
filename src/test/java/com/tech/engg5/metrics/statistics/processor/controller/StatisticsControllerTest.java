package com.tech.engg5.metrics.statistics.processor.controller;

import com.tech.engg5.metrics.statistics.processor.IntegrationTestBase;
import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import com.tech.engg5.metrics.statistics.processor.metrics.repository.MetricsRepositoryImpl;
import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.CsvBatchSummary;
import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.MetricsBatchSummary;
import com.tech.engg5.metrics.statistics.processor.statistics.model.mongo.Statistics;
import com.tech.engg5.metrics.statistics.processor.statistics.repository.CsvBatchSummaryRepositoryImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.tech.engg5.metrics.statistics.processor.util.FileUtility.readFile;
import static com.tech.engg5.metrics.statistics.processor.util.FileUtility.readStringAsList;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StatisticsControllerTest extends IntegrationTestBase {

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ReactiveMongoOperations reactiveMongoOperations;

  @SpyBean
  MetricsRepositoryImpl metricsRepository;

  @SpyBean
  CsvBatchSummaryRepositoryImpl csvBatchSummaryRepository;

  private static final String TEST_DATA_METRICS_TYPE_A = "fixtures/database/metrics/metrics-batch-events-A.json";

  @BeforeEach
  @AfterEach
  void setUp() {
    reactiveMongoOperations.remove(Metrics.class).all().block();
    reactiveMongoOperations.remove(CsvBatchSummary.class).all().block();
    reactiveMongoOperations.remove(Statistics.class).all().block();
  }

  @Test
  @SneakyThrows
  @DisplayName("Verify that response contains Batch Statistics Type A")
  void shouldGenerateBatchStatisticsTypeA() {
    String metricsDataFile = readFile(TEST_DATA_METRICS_TYPE_A).replace("{{timestamp}}",
      Instant.now().minus(Duration.ofHours(1)).toString());

    List<Metrics> metricsDataList = readStringAsList(metricsDataFile, Metrics[].class);
    metricsDataList.forEach(metrics -> reactiveMongoOperations.save(metrics, "event-metrics").block());

    given(metricsRepository.findMetricsBatchSummary(any())).willReturn(Mono.just(MetricsBatchSummary.builder()
      .failedCount(2L)
      .successCount(3L)
      .build()));
    given(csvBatchSummaryRepository.findBatchRecordCountBySummaryId(any())).willReturn(Mono.just(CsvBatchSummary
      .builder()
      .fileName("CSV_BATCH_17072024_171759.csv")
      .fileDate(Instant.parse("2024-07-17T07:38:49Z"))
      .recordCount(5L)
      .build()));

    var response = webTestClient
      .get()
      .uri("/event/statistics/batch")
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(String.class)
      .returnResult().getResponseBody();

    assertThat(response).containsSubsequence("\"batchId\":\"f1234221-da02-45a9-811c-802f5881e39e\","
      + "\"batchType\":\"CSV_BATCH\",\"fileName\":\"CSV_BATCH_17072024_171759.csv\",\"eventReceived\":5,"
      + "\"eventFailed\":2,\"eventSuccess\":3,\"batchDate\":\"2024-07-17T07:38:49Z\"");
  }

  @Test
  @SneakyThrows
  @DisplayName("Verify that response status code is 404 when no valid batchId is found.")
  void shouldReturn404WhenNoValidBatchIdFound() {
    var response = webTestClient
      .get()
      .uri("/event/statistics/batch")
      .exchange()
      .expectStatus()
      .is4xxClientError()
      .expectBody(String.class)
      .returnResult().getResponseBody();

    assert Objects.requireNonNull(response).contains("No batch records found for the given time-range.");
  }
}

package com.tech.engg5.metrics.statistics.processor;

import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import org.assertj.core.api.RecursiveComparisonAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import java.time.Duration;
import java.util.List;

import static java.util.Arrays.asList;
import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTestBase {

  protected static final List<String> DEFAULT_FIELDS_TO_IGNORE_METRICS =
    asList(Metrics.Fields.createdTs, Metrics.Fields.lastUpdatedTs);

  protected static final Duration EXPECTING_MESSAGES_TIMEOUT = Duration.ofSeconds(500);

  @Autowired
  protected ReactiveMongoOperations reactiveMongoOperations;

  @SafeVarargs
  protected final <T> void thenExpectDatabaseEntriesMetrics(Class<T> type, T... expectedEntries) {
    thenExpectDatabaseEntries(type, DEFAULT_FIELDS_TO_IGNORE_METRICS, expectedEntries);
  }

  @SafeVarargs
  protected final <T> void thenExpectDatabaseEntries(Class<T> type, List<String> fieldsToIgnore, T... expectedEntries) {
    await().atMost(EXPECTING_MESSAGES_TIMEOUT)
      .until(() -> reactiveMongoOperations.findAll(type).collectList().block().size() == expectedEntries.length);

    List<T> entriesInCollection = reactiveMongoOperations.findAll(type).collectList().block();
    RecursiveComparisonAssert<?> assertion = assertThat(entriesInCollection).usingRecursiveComparison()
      .ignoringAllOverriddenEquals()
      .ignoringFields(fieldsToIgnore.toArray(String[]::new))
      .ignoringCollectionOrder();

    assertion.isEqualTo(asList(expectedEntries));
  }
}

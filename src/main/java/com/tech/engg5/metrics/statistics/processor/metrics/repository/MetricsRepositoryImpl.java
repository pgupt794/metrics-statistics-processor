package com.tech.engg5.metrics.statistics.processor.metrics.repository;

import com.tech.engg5.metrics.statistics.processor.metrics.enums.ComponentStatus;
import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.MetricsBatchSummary;
import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.MetricsRealTimeSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@RequiredArgsConstructor
public class MetricsRepositoryImpl implements MetricsRepositoryCustom {

  private final ReactiveMongoTemplate mongoTemplate;

  @Override
  public Mono<Metrics> createOrUpdateMetrics(Metrics metrics) {
    LOG.info("Inside createOrUpdateMetrics, correlationId - [{}].", metrics.getCorrelationId());
    Query findQuery = query(where(Metrics.Fields.correlationId)
      .is(metrics.getCorrelationId()));

    Update updateDefinition = new Update()
      .setOnInsert(Metrics.Fields.correlationId, metrics.getCorrelationId())
      .setOnInsert(Metrics.Fields.batchId, metrics.getBatchId())
      .setOnInsert(Metrics.Fields.batchType, metrics.getBatchType())
      .setOnInsert(Metrics.Fields.mappingId, metrics.getMappingId())
      .set(Metrics.Fields.component, metrics.getComponent())
      .set(Metrics.Fields.failure, metrics.getFailure())
      .setOnInsert(Metrics.Fields.createdTs, metrics.getCreatedTs())
      .set(Metrics.Fields.lastUpdatedTs, metrics.getLastUpdatedTs());

    return mongoTemplate.findAndModify(findQuery, updateDefinition, options().upsert(true).returnNew(true),
      Metrics.class)
      .doOnSuccess(success -> LOG.info("Metrics with correlationId - [{}] upserted.", metrics.getCorrelationId()));
  }

  @Override
  public Flux<Metrics> findDistinctBatchIdBetweenTimeRange(Instant fromTs, Instant toTs) {
    LOG.info("Fetching distinct batchId(s) between [{}] to [{}] timestamp.", fromTs, toTs);

    return mongoTemplate.query(Metrics.class)
      .matching(query(where(Metrics.Fields.createdTs).gte(fromTs).lte(toTs)
        .and(Metrics.Fields.batchId).ne(null)))
      .all().distinct(Metrics::getBatchId);
  }

  @Override
  public Mono<MetricsBatchSummary> findMetricsBatchSummary(String summaryId) {
    LOG.info("Inside findMetricsBatchSummary method for batchId - [{}].", summaryId);
    MatchOperation matchOperation = Aggregation.match(Criteria.where(Metrics.Fields.batchId).is(summaryId));
    GroupOperation groupOperation = Aggregation.group()
      .sum(ConditionalOperators.when(
        Criteria.where("component.status").is(ComponentStatus.PROCESSING_FAILED.toString())
      ).then(1).otherwise(0)).as(MetricsBatchSummary.Fields.failedCount)
      .sum(ConditionalOperators.when(
        Criteria.where("component.status").is(ComponentStatus.PROCESSING_COMPLETED.toString())
      ).then(1).otherwise(0)).as(MetricsBatchSummary.Fields.successCount);

    Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);
    return mongoTemplate.aggregate(aggregation, "event-metrics", MetricsBatchSummary.class)
      .singleOrEmpty();
  }

  @Override
  public Flux<String> findDistinctErrorMessagesBetweenTimeRange(Instant from, Instant to) {
    LOG.info("Fetching distinct error-messages between [{}] to [{}].", from, to);

    Query searchQuery = query(where(Metrics.Fields.createdTs).gte(from).lte(to)
      .and(Metrics.Fields.batchId).is(null));
    Set<String> seenErrorMessages = new HashSet<>();

    return mongoTemplate.find(searchQuery, Metrics.class, "event-metrics")
      .filter(metric -> ObjectUtils.isNotEmpty(metric.getFailure())
      && StringUtils.hasText(metric.getFailure().getErrorMessage())
      && seenErrorMessages.add(metric.getFailure().getErrorMessage().toLowerCase()))
    .map(metric -> metric.getFailure().getErrorMessage())
    .distinct();
  }

  @Override
  public Flux<MetricsRealTimeSummary> findMetricsRealTimeSummary(Instant from, Instant to, String errorMessage) {
    LOG.info("Inside findMetricsRealTimeSummary method for errorMessage - [{}].", errorMessage);

    Criteria criteria = Criteria.where(Metrics.Fields.createdTs).gte(from).lte(to)
      .and(Metrics.Fields.batchId).is(null)
      .and("failure.errorMessage").is(errorMessage);
    AggregationOperation match = Aggregation.match(criteria);
    AggregationOperation group = Aggregation.group(Metrics.Fields.mappingId)
      .count().as("failedRecordCount")
      .first("failure.errorMessage").as("errorMessage")
      .first(Metrics.Fields.mappingId).as(MetricsRealTimeSummary.Fields.mappingId);
    Aggregation aggregation = Aggregation.newAggregation(match, group);

    return mongoTemplate.aggregate(aggregation, "event-metrics", MetricsRealTimeSummary.class);
  }
}

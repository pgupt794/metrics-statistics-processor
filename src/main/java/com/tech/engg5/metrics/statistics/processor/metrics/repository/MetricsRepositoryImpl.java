package com.tech.engg5.metrics.statistics.processor.metrics.repository;

import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

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
      .set(Metrics.Fields.component, metrics.getComponent())
      .set(Metrics.Fields.failure, metrics.getFailure())
      .setOnInsert(Metrics.Fields.createdTs, metrics.getCreatedTs())
      .set(Metrics.Fields.lastUpdatedTs, metrics.getLastUpdatedTs());

    return mongoTemplate.findAndModify(findQuery, updateDefinition, options().upsert(true).returnNew(true),
      Metrics.class)
      .doOnSuccess(success -> LOG.info("Metrics with correlationId - [{}] upserted.", metrics.getCorrelationId()));
  }
}

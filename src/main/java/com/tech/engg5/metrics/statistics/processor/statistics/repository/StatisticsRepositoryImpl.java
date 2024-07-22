package com.tech.engg5.metrics.statistics.processor.statistics.repository;

import com.tech.engg5.metrics.statistics.processor.statistics.model.mongo.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@RequiredArgsConstructor
public class StatisticsRepositoryImpl implements StatisticsRepositoryCustom {

  private final ReactiveMongoTemplate mongoTemplate;

  @Override
  public Mono<Statistics> createOrUpdateBatchStatistics(Statistics statistics) {
    LOG.info("Inside createOrUpdateBatchStatistics. BatchId - [{}].", statistics.getBatchId());

    Query query = query(where(Statistics.Fields.batchId).is(statistics.getBatchId()));
    Update updateDefinition = new Update()
      .setOnInsert(Statistics.Fields.batchId, statistics.getBatchId())
      .setOnInsert(Statistics.Fields.batchType, statistics.getBatchType())
      .setOnInsert(Statistics.Fields.fileName, statistics.getFileName())
      .setOnInsert(Statistics.Fields.createdTs, Instant.now())
      .set(Statistics.Fields.eventReceived, statistics.getEventReceived())
      .set(Statistics.Fields.eventFailed, statistics.getEventFailed())
      .set(Statistics.Fields.eventSuccess, statistics.getEventSuccess())
      .set(Statistics.Fields.batchDate, statistics.getBatchDate())
      .set(Statistics.Fields.lastUpdatedTs, Instant.now());

    return this.mongoTemplate.findAndModify(query, updateDefinition, options().upsert(true).returnNew(true),
      Statistics.class)
      .doOnSuccess(success -> LOG.info("Statistics upserted successfully for batchId - [{}], statistics - [{}]",
        success.getBatchId(), success));
  }

  @Override
  public Mono<Statistics> createOrUpdateRealTimeFailureStatistics(Statistics statistics) {
    LOG.info("Inside createOrUpdateRealTimeFailureStatistics. ErrorMessage - [{}], mappingId - [{}]",
      statistics.getErrorMessage(), statistics.getEventType());

    Query query = query(where(Statistics.Fields.errorMessage).is(statistics.getErrorMessage())
      .and(Statistics.Fields.eventType).is(statistics.getEventType()));
    Update updateDefinition = new Update()
      .setOnInsert(Statistics.Fields.errorMessage, statistics.getErrorMessage())
      .set(Statistics.Fields.eventType, statistics.getEventType())
      .set(Statistics.Fields.eventFailed, statistics.getEventFailed())
      .setOnInsert(Statistics.Fields.capturedFrom, statistics.getCapturedFrom())
      .setOnInsert(Statistics.Fields.capturedTo, statistics.getCapturedTo())
      .setOnInsert(Statistics.Fields.createdTs, Instant.now())
      .set(Statistics.Fields.lastUpdatedTs, Instant.now());

    return mongoTemplate.findAndModify(query, updateDefinition, options().upsert(true).returnNew(true), Statistics.class)
      .doOnSuccess(success -> LOG.info("Statistics for real-time failure successfully for errorMessage [{}], "
        + "mappingId - [{}].", statistics.getErrorMessage(), statistics.getEventType()));
  }

  @Override
  public Mono<Statistics> findByBatchId(String batchId) {
    Query query = query(where(Statistics.Fields.batchId).is(batchId));
    return this.mongoTemplate.findOne(query, Statistics.class);
  }
}

package com.tech.engg5.metrics.statistics.processor.statistics.repository;

import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.CsvBatchSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@RequiredArgsConstructor
public class CsvBatchSummaryRepositoryImpl implements CsvBatchSummaryRepositoryCustom {
  private final ReactiveMongoTemplate mongoTemplate;

  @Override
  public Mono<CsvBatchSummary> findBatchRecordCountBySummaryId(String summaryId) {
    LOG.info("Fetching csv-batch-summary with summaryId - [{}] for details.", summaryId);

    Query fetchQuery = query(where(CsvBatchSummary.Fields.summaryId).is(summaryId));
    return mongoTemplate.findOne(fetchQuery, CsvBatchSummary.class);
  }
}

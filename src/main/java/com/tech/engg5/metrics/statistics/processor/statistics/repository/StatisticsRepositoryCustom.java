package com.tech.engg5.metrics.statistics.processor.statistics.repository;

import com.tech.engg5.metrics.statistics.processor.statistics.model.mongo.Statistics;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StatisticsRepositoryCustom {

  Mono<Statistics> createOrUpdateBatchStatistics(Statistics statistics);
  Mono<Statistics> findByBatchId(String batchId);
}

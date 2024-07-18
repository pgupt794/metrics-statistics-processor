package com.tech.engg5.metrics.statistics.processor.metrics.repository;

import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.MetricsBatchSummary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface MetricsRepositoryCustom {

  Mono<Metrics> createOrUpdateMetrics(Metrics metrics);

  Flux<Metrics> findDistinctBatchIdBetweenTimeRange(Instant fromTs, Instant toTs);

  Mono<MetricsBatchSummary> findMetricsBatchSummary(String summaryId);
}

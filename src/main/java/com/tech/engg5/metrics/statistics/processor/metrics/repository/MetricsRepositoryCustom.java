package com.tech.engg5.metrics.statistics.processor.metrics.repository;

import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MetricsRepositoryCustom {

  Mono<Metrics> createOrUpdateMetrics(Metrics metrics);
}

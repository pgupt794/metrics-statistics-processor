package com.tech.engg5.metrics.statistics.processor.metrics.repository;

import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MetricsRepository
  extends ReactiveMongoRepository<Metrics, String>, MetricsRepositoryCustom {

  Mono<Metrics> findMetricsByCorrelationId(String correlationId);
}

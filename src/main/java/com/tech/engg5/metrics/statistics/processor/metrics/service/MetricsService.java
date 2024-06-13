package com.tech.engg5.metrics.statistics.processor.metrics.service;

import com.tech.engg5.metrics.statistics.processor.metrics.exception.DatabaseException;
import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import com.tech.engg5.metrics.statistics.processor.metrics.repository.MetricsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MetricsService {

  MetricsRepository metricsRepository;

  public Mono<Metrics> handleMetricsRequest(Metrics request) {
    LOG.info("Metrics request received. correlationId - [{}], component - [{}], status - [{}]",
      request.getCorrelationId(), request.getComponent().getName(), request.getComponent().getStatus());

    return Mono.just(request)
      .filterWhen(req -> this.validateTimeStamp(req.getCorrelationId(), req.getLastUpdatedTs()))
      .flatMap(this::saveMetricsRequest);
  }

  private Mono<Boolean> validateTimeStamp(String correlationId, Instant lastUpdatedTs) {
    return metricsRepository.findMetricsByCorrelationId(correlationId)
      .map(record -> {
        boolean validTimeStamp = lastUpdatedTs.isAfter(record.getLastUpdatedTs());
        if (!validTimeStamp) {
          LOG.info("Request lastUpdatedTs is smaller than the existing record lastUpdatedTs. "
            + "Returning without saving the request.");
          return false;
        }
        return validTimeStamp;
      }).defaultIfEmpty(true);
  }

  public Mono<Metrics> saveMetricsRequest(Metrics metricsRequest) {
    return metricsRepository.createOrUpdateMetrics(metricsRequest)
      .onErrorResume(err -> {
        LOG.error("Failed to create or update metric record, correlationId - [{}]", metricsRequest.getCorrelationId());
        return Mono.error(new DatabaseException("Database Insert Error.", err));
      });
  }
}

package com.tech.engg5.metrics.statistics.processor.metrics.controller;

import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import com.tech.engg5.metrics.statistics.processor.metrics.service.MetricsService;
import com.tech.engg5.metrics.statistics.processor.metrics.utils.MetricsDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/event/metrics")
public class MetricsController {

  MetricsDeserializer metricsDeserializer;
  MetricsService metricsService;

  public MetricsController(MetricsDeserializer metricsDeserializer, MetricsService metricsService) {
    this.metricsDeserializer = metricsDeserializer;
    this.metricsService = metricsService;
  }

  @PostMapping
  public Mono<ResponseEntity<String>> handleMetrics(@RequestBody String metricsRawRequest) {
    return Mono.just(metricsRawRequest)
      .handle(metricsDeserializer)
      .flatMap(metricsService::handleMetricsRequest)
      .map(this::sendSuccessResponse);
  }

  private ResponseEntity<String> sendSuccessResponse(Metrics metrics) {
    LOG.info("Event saved successfully. correlationId - [{}].", metrics.getCorrelationId());
    return ResponseEntity.status(HttpStatus.OK).body("Saved Success.");
  }
}

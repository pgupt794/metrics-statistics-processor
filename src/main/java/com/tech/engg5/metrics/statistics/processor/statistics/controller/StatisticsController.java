package com.tech.engg5.metrics.statistics.processor.statistics.controller;

import com.tech.engg5.metrics.statistics.processor.statistics.model.mongo.Statistics;
import com.tech.engg5.metrics.statistics.processor.statistics.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/event/statistics")
public class StatisticsController {

  @Autowired
  StatisticsService statisticsService;

  @GetMapping("/batch")
  public Flux<Statistics> createBatchStatistics() {

    Instant to = Instant.now();
    Instant from = to.minus(Duration.ofHours(24));

    return statisticsService.findBatchIdBetweenTimeRange(from, to)
      .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
        "No batch records found for the given time-range.")));
  }
}

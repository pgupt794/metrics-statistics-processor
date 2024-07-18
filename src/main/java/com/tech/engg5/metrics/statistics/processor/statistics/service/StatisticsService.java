package com.tech.engg5.metrics.statistics.processor.statistics.service;

import com.tech.engg5.metrics.statistics.processor.metrics.enums.BatchType;
import com.tech.engg5.metrics.statistics.processor.metrics.exception.DatabaseException;
import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import com.tech.engg5.metrics.statistics.processor.metrics.repository.MetricsRepository;
import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.CsvBatchSummary;
import com.tech.engg5.metrics.statistics.processor.statistics.model.mongo.Statistics;
import com.tech.engg5.metrics.statistics.processor.statistics.repository.CsvBatchSummaryRepository;
import com.tech.engg5.metrics.statistics.processor.statistics.repository.StatisticsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {

  MetricsRepository metricsRepository;
  StatisticsRepository statisticsRepository;
  CsvBatchSummaryRepository csvBatchSummaryRepository;

  public Flux<Statistics> findBatchIdBetweenTimeRange(Instant from, Instant to) {
    LOG.info("Inside StatisticsService.findBatchIdBetweenTimeRange method.");
    return metricsRepository.findDistinctBatchIdBetweenTimeRange(from, to)
      .collectList()
      .flatMapMany(Flux::fromIterable)
      .concatMap(this::generateStatisticsBasedOnBatchType)
      .onErrorResume(err -> {
        LOG.error("Error occurred while fetching batchId(s) between time-range.");
        return Mono.error(err);
      });
  }

  public Mono<Statistics> generateStatisticsBasedOnBatchType(Metrics metrics) {
    LOG.info("Statistics request for batchType - [{}], batchId - [{}].", metrics.getBatchType(), metrics.getBatchId());
    if (BatchType.CSV_BATCH.name().equalsIgnoreCase(metrics.getBatchType().name())) {
      val batchSummary = csvBatchSummaryRepository.findBatchRecordCountBySummaryId(metrics.getBatchId());
      val batchRecordCount = batchSummary.map(CsvBatchSummary::getRecordCount);
      val batchDate = batchSummary.map(CsvBatchSummary::getFileDate);
      val batchFileName = batchSummary.map(CsvBatchSummary::getFileName);

      return Mono.just(batchSummary)
        .flatMap(summary -> this.generateStatistics(metrics, batchRecordCount, batchDate, metrics.getBatchType().name(),
          batchFileName))
        .flatMap(this::saveBatchStatistics)
        .onErrorResume(err -> {
          LOG.error("Error occurred while fetching records with batchId between time-range for batchType [{}].",
            metrics.getBatchType());
          return Mono.error(err);
        });

    } else {
      return Mono.error(new IllegalStateException("Unsupported Batch-Type."));
    }
  }

  public Mono<Statistics> generateStatistics(Metrics metrics, Mono<Long> recordCount, Mono<Instant> batchDate,
    String batchType, Mono<String> fileName) {
    LOG.info("Generating statistics for batchId - [{}], batchType - [{}].", metrics.getBatchId(), metrics.getBatchType());

    if (metrics.getBatchId().isEmpty()) {
      LOG.warn("No batchId found in the metrics for statistics.");
      return Mono.empty();
    } else {
      val metricsBatchSummary= metricsRepository.findMetricsBatchSummary(metrics.getBatchId());

      return Mono.zip(metricsBatchSummary, recordCount, batchDate, fileName)
        .map(value -> Statistics.builder()
          .batchId(metrics.getBatchId())
          .batchType(batchType)
          .fileName(value.getT4())
          .eventReceived(value.getT2())
          .eventFailed(value.getT1().getFailedCount())
          .eventSuccess(value.getT1().getSuccessCount())
          .batchDate(value.getT3())
          .build());
    }
  }

  public Mono<Statistics> saveBatchStatistics(Statistics statistics) {
    return statisticsRepository.createOrUpdateBatchStatistics(statistics)
      .onErrorResume(err -> {
        LOG.error("Error while creating/updating batch statistics.");
        return Mono.error(new DatabaseException("Batch statistics database insert error.", err));
      });
  }
}

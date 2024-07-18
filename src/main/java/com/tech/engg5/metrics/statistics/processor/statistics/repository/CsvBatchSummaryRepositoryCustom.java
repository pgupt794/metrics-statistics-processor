package com.tech.engg5.metrics.statistics.processor.statistics.repository;

import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.CsvBatchSummary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CsvBatchSummaryRepositoryCustom {
  Mono<CsvBatchSummary> findBatchRecordCountBySummaryId(String summaryId);
}

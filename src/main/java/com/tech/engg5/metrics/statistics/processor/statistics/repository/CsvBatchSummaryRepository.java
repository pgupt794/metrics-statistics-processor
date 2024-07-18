package com.tech.engg5.metrics.statistics.processor.statistics.repository;

import com.tech.engg5.metrics.statistics.processor.statistics.model.domain.CsvBatchSummary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvBatchSummaryRepository extends
  ReactiveMongoRepository<CsvBatchSummary, String>, CsvBatchSummaryRepositoryCustom {
}

package com.tech.engg5.metrics.statistics.processor.statistics.repository;

import com.tech.engg5.metrics.statistics.processor.statistics.model.mongo.Statistics;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository
  extends ReactiveMongoRepository<Statistics, String>, StatisticsRepositoryCustom {
}

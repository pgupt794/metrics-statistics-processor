package com.tech.engg5.metrics.statistics.processor.metrics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tech.engg5.metrics.statistics.processor.metrics.enums.BatchType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "event-metrics")
public class Metrics {

  @Id
  String correlationId;
  String batchId;
  BatchType batchType;
  String mappingId;
  Component component;
  Failure failure;
  Instant createdTs;
  Instant lastUpdatedTs;
}

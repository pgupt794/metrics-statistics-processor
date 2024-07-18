package com.tech.engg5.metrics.statistics.processor.statistics.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "csv-batch-summary")
public class CsvBatchSummary {
  @Id
  String summaryId;
  String fileName;
  String fileCreatedBy;
  Instant fileDate;
  Long recordCount;
  String status;
  String detailedStatus;
  Instant createdTs;
  @LastModifiedDate
  Instant lastUpdatedTs;
}

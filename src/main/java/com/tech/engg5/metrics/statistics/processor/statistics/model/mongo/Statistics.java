package com.tech.engg5.metrics.statistics.processor.statistics.model.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "event-statistics")
public class Statistics {

  String batchId;
  String batchType;
  String eventType;
  String fileName;
  String errorMessage;
  Long eventReceived;
  Long eventFailed;
  Long eventSuccess;
  Instant batchDate;
  Instant capturedFrom;
  Instant capturedTo;
  Instant createdTs;
  Instant lastUpdatedTs;
}

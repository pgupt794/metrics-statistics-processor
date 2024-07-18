package com.tech.engg5.metrics.statistics.processor.statistics.model.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@FieldNameConstants
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetricsBatchSummary {
  long failedCount;
  long successCount;
}

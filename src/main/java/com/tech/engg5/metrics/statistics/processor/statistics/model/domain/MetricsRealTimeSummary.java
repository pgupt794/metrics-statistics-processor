package com.tech.engg5.metrics.statistics.processor.statistics.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@FieldNameConstants
@RequiredArgsConstructor
@AllArgsConstructor
public class MetricsRealTimeSummary {
  String errorMessage;
  String mappingId;
  Long failedRecordCount;
}

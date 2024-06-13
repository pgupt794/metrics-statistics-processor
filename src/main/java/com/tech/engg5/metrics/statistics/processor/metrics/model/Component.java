package com.tech.engg5.metrics.statistics.processor.metrics.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tech.engg5.metrics.statistics.processor.metrics.enums.ComponentName;
import com.tech.engg5.metrics.statistics.processor.metrics.enums.ComponentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Component {

  ComponentName name;
  ComponentStatus status;
}

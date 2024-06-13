package com.tech.engg5.metrics.statistics.processor.metrics.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.engg5.metrics.statistics.processor.metrics.model.Metrics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.SynchronousSink;

import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MetricsDeserializer
  implements BiConsumer<String, SynchronousSink<Metrics>> {

  ObjectMapper objectMapper;


  @Override
  public void accept(String jsonAsString, SynchronousSink<Metrics> sink) {
    try {
      sink.next(this.deserializeRequest(jsonAsString));
    } catch (Exception exc) {
      sink.error(exc);
    }
  }

  @SneakyThrows
  private Metrics deserializeRequest(String jsonAsString) {
    return objectMapper.readValue(jsonAsString, Metrics.class);
  }
}

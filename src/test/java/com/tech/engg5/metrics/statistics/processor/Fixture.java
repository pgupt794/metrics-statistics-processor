package com.tech.engg5.metrics.statistics.processor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static wiremock.org.apache.commons.lang3.StringUtils.join;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public enum Fixture {
  CONTROLLER_REQUEST("controller/requests"),
  CONTROLLER_RESPONSES("controller/responses"),
  DATABASE("database");

  String path;

  @SneakyThrows
  public String loadFixture(String filename, SubPath... subPaths) {
    String fixturePath = "fixtures/" + this.path + '/' + join(subPaths, '/') + '/' + filename;
    try (InputStream inputStream = new ClassPathResource(fixturePath).getInputStream()) {
      return new String(IOUtils.toByteArray(inputStream), StandardCharsets.UTF_8);
    }
  }

  @RequiredArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  public enum SubPath {
    METRICS("metrics");

    String subPath;

    @Override
    public String toString() {
      return subPath;
    }
  }
}

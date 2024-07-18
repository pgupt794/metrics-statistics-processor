package com.tech.engg5.metrics.statistics.processor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.nio.charset.Charset;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtility {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
    .registerModule(new JavaTimeModule());

  @SneakyThrows
  public static String readFile(String resourceFilePath) {
    return Resources.toString(Resources.getResource(resourceFilePath), Charset.defaultCharset());
  }

  @SneakyThrows
  public static <T> List<T> readStringAsList(String content, Class<T[]> clazz) {
    return Lists.newArrayList(OBJECT_MAPPER.readValue(content, clazz));
  }
}

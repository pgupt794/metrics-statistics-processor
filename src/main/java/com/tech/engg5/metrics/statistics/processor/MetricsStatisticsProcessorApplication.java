package com.tech.engg5.metrics.statistics.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class MetricsStatisticsProcessorApplication {
  public static void main(String[] args) {
    ReactorDebugAgent.init();
    System.setProperty("APP_ID", "1000160");
    System.setProperty("APP_NAME", "metrics-statistics-processor");
    SpringApplication application = new SpringApplication(MetricsStatisticsProcessorApplication.class);
    application.run(args);
  }
}

package com.reactivespring.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

//@Configuration
public class TopicConfig {
  @Value(value = "${kafka.bootstrapAddress}")
  private String bootStrapAddress;

  @Value(value = "${general.topic.name}")
  private String generalTopic;

  @Value(value = "${movie.topic.name}")
  private String movieTopicName;

  @Bean
  public NewTopic movieTopic() {
    return TopicBuilder.name(movieTopicName)
        .partitions(1)
        .replicas(1)
        .build();
  }
  @Bean
  public NewTopic generalTopic() {
    return TopicBuilder.name(generalTopic)
        .partitions(1)
        .replicas(1)
        .build();
  }

}

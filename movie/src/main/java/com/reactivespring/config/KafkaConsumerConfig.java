package com.reactivespring.config;

import com.reactivespring.deserilizer.MovieDtoDeserializer;
import com.reactivespring.dto.MovieDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {
  @Value(value = "${kafka.bootstrapAddress}")
  private String bootStrapAddress;

  @Value(value = "${general.topic.group.id}")
  private String groupId;

  @Value(value = "${movie.topic.group.id}")
  private String movieGroupId;

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String>
  kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory
        = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, MovieDto> movieDtoConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, movieGroupId);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        MovieDtoDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(props,
        new StringDeserializer(),
        new MovieDtoDeserializer());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, MovieDto>
  movieDtoConcurrentKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, MovieDto> factory
        = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(movieDtoConsumerFactory());
    return factory;
  }
}
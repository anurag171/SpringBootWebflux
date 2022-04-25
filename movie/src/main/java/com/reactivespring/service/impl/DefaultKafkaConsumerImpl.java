package com.reactivespring.service.impl;

import com.reactivespring.dto.MovieDto;
import com.reactivespring.service.KafkaConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultKafkaConsumerImpl implements KafkaConsumer {



  @Override
  @KafkaListener(topics = {"${general.topic.name}"}
        ,groupId = "${general.topic.group.id}")
  public void consume(String message) {
    log.info(String.format("Message recieved -> %s", message));
  }

  @Override
  @KafkaListener(topics = {"${movie.topic.name}"}
      ,groupId = "${movie.topic.group.id}")
  public void consume(MovieDto message) {
    log.info(String.format("MovieDto recieved -> %s", message));
  }
}

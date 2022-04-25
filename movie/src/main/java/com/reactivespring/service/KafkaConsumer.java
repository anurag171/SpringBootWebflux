package com.reactivespring.service;

import com.reactivespring.dto.MovieDto;

public interface KafkaConsumer {

  void consume(String message);
  void consume(MovieDto message);

}

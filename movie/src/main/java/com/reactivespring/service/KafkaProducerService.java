package com.reactivespring.service;

import com.reactivespring.dto.MovieDto;

public interface KafkaProducerService {

  void sendMessage(String message);

  void saveCreateMovie(MovieDto movieDto);

}

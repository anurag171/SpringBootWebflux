package com.reactivespring.service.impl;

import com.reactivespring.dto.MovieDto;
import com.reactivespring.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
 
@Service
@Slf4j
public class DefaultKafKaProducerServiceImpl implements KafkaProducerService {

   
  //1. General topic with a string payload
   
  @Value(value = "${general.topic.name}")
    private String topicName;
   
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
 
  //2. Topic with user object payload
     
    @Value(value = "${movie.topic.name}")
    private String movieTopicName;
     
    @Autowired
    private KafkaTemplate<String, MovieDto> movieDtoKafkaTemplate;
   
  public void sendMessage(String message) 
  {
    ListenableFuture<SendResult<String, String>> future 
      = this.kafkaTemplate.send(topicName, message);
     
    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
              log.info("Sent message: " + message 
                  + " with offset: " + result.getRecordMetadata().offset() + " on topic" + topicName);
            }
 
            @Override
            public void onFailure(Throwable ex) {
              log.error("Unable to send message : " + message, ex);
            }
       });
  }
   
  public void saveCreateMovie(MovieDto movieDto)
  {
    ListenableFuture<SendResult<String, MovieDto>> future
      = this.movieDtoKafkaTemplate.send(movieTopicName, movieDto);
     
    future.addCallback(new ListenableFutureCallback<SendResult<String, MovieDto>>() {
            @Override
            public void onSuccess(SendResult<String, MovieDto> result) {
              log.info("Movie details: "
                  + movieDto + " with offset: " + result.getRecordMetadata().offset());
            }
 
            @Override
            public void onFailure(Throwable ex) {
              log.error("Movie details : " + movieDto, ex);
            }
       });
  }
}
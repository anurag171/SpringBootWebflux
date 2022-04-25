package com.reactivespring.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reactivespring.dto.MovieDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class MovieDtoSerializer implements Serializer<MovieDto> {
  private final ObjectMapper objectMapper = new ObjectMapper();

  public MovieDtoSerializer(){
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
  @Override
  public byte[] serialize(String s, MovieDto movieDto) {
    try {
      if (movieDto == null){
        log.error("Null received at serializing");
        return new byte[]{};
      }
      log.info("Serializing...");
      return objectMapper.writeValueAsBytes(movieDto);
    } catch (Exception e) {
      log.error("Exception while serializing [{}]", e.getMessage());
      throw new SerializationException("Error when serializing MessageDto to byte[]");
    }
  }
}

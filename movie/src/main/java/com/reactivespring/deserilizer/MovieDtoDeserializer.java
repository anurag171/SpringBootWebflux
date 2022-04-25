package com.reactivespring.deserilizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivespring.dto.MovieDto;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class MovieDtoDeserializer implements Deserializer<MovieDto> {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public MovieDto deserialize(String s, byte[] data) {
    try {
      if (data == null){
        log.error("Null received at deserializing");
        return null;
      }
     log.info("Deserializing...");
      return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), MovieDto.class);
    } catch (Exception e) {
      throw new SerializationException("Error when deserializing byte[] to MessageDto");
    }
  }
}

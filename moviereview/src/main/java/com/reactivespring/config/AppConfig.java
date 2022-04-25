package com.reactivespring.config;

import com.reactivespring.convertor.MovieReviewDtoToMovieReviewConvertor;
import com.reactivespring.convertor.MovieReviewToMovieReviewDtoConvertor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class AppConfig {



  @Bean
  public ConversionService conversionService () {
    DefaultConversionService service = new DefaultConversionService();
    service.addConverter(new MovieReviewDtoToMovieReviewConvertor());
    service.addConverter(new MovieReviewToMovieReviewDtoConvertor());
    return service;
  }

}

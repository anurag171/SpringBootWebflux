package com.reactivesprig.config;

import com.reactivesprig.convertor.MovieInfoDtoToMovieInfoConvertor;
import com.reactivesprig.convertor.MovieInfoToMovieInfoDtoConvertor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class AppConfig {



  @Bean
  public ConversionService conversionService () {
    DefaultConversionService service = new DefaultConversionService();
    service.addConverter(new MovieInfoDtoToMovieInfoConvertor());
    service.addConverter(new MovieInfoToMovieInfoDtoConvertor());
    return service;
  }

}

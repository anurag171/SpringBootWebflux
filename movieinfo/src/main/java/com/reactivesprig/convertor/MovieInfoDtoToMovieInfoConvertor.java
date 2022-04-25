package com.reactivesprig.convertor;

import com.reactivesprig.domain.MovieInfo;
import com.reactivesprig.dto.MovieInfoDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class MovieInfoDtoToMovieInfoConvertor implements Converter<MovieInfoDto, MovieInfo> {

  @Override
  public MovieInfo convert(MovieInfoDto source) {
    MovieInfo movieInfo = new MovieInfo();
    BeanUtils.copyProperties(source,movieInfo);
    return movieInfo;
  }


}

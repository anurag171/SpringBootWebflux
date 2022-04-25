package com.reactivesprig.convertor;

import com.reactivesprig.domain.MovieInfo;
import com.reactivesprig.dto.MovieInfoDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class MovieInfoToMovieInfoDtoConvertor implements Converter<MovieInfo, MovieInfoDto> {

  @Override
  public MovieInfoDto convert(@NonNull MovieInfo source) {
    MovieInfoDto movieInfoDto = new MovieInfoDto();
    BeanUtils.copyProperties(source,movieInfoDto);
    return movieInfoDto;
  }


}

package com.reactivespring.convertor;

import com.reactivespring.domain.Review;
import com.reactivespring.dto.MovieReviewDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class MovieReviewDtoToMovieReviewConvertor implements Converter<MovieReviewDto, Review> {

  @Override
  public Review convert(MovieReviewDto source) {
    Review movieInfo = new Review();
    BeanUtils.copyProperties(source,movieInfo);
    return movieInfo;
  }


}

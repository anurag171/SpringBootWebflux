package com.reactivespring.convertor;

import com.reactivespring.domain.Review;
import com.reactivespring.dto.MovieReviewDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class MovieReviewToMovieReviewDtoConvertor implements Converter<Review,MovieReviewDto > {

  @Override
  public MovieReviewDto convert(@NonNull Review source) {
    MovieReviewDto movieReviewDto = new MovieReviewDto();
    BeanUtils.copyProperties(source,movieReviewDto);
    return movieReviewDto;
  }
}

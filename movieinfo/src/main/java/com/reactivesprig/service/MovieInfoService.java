package com.reactivesprig.service;

import com.reactivesprig.dto.MovieInfoDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoService {

  Mono<MovieInfoDto> addMovie(MovieInfoDto movieInfoDto);

  Flux<MovieInfoDto> getAllMovies();

  Mono<MovieInfoDto> getMovieById(String id);

  Mono<MovieInfoDto> updateMovie(MovieInfoDto movieInfoDto, String id);

  Mono<Void> deleteMovie(String id);

  Flux<MovieInfoDto> getAllMoviesInfoByYear(Integer year);
}

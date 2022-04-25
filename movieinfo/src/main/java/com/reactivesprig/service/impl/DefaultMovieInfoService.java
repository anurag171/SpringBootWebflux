package com.reactivesprig.service.impl;

import com.reactivesprig.domain.MovieInfo;
import com.reactivesprig.dto.MovieInfoDto;
import com.reactivesprig.repository.MovieInfoRepository;
import com.reactivesprig.service.MovieInfoService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultMovieInfoService implements MovieInfoService {

  private final ConversionService conversionService;

  private final MovieInfoRepository movieInfoRepository;

  @Override
  public Mono<MovieInfoDto> addMovie(@NonNull MovieInfoDto movieInfoDto) {
    MovieInfo movieInfo = conversionService.convert(movieInfoDto,MovieInfo.class);
    if(Objects.nonNull(movieInfo)) {
      return movieInfoRepository.save(movieInfo)
          .flatMap(movieInfo1 -> Mono.just((MovieInfoDto) conversionService.convert(movieInfo1,
              TypeDescriptor.valueOf(MovieInfo.class),TypeDescriptor.valueOf(MovieInfoDto.class)))
          );
    }else{
      return Mono.empty();
    }
  }

  @Override
  public Flux<MovieInfoDto> getAllMovies() {
    return movieInfoRepository.findAll().flatMap(movieInfo ->
     Mono.just((MovieInfoDto) conversionService.convert(movieInfo,
         TypeDescriptor.valueOf(MovieInfo.class),TypeDescriptor.valueOf(MovieInfoDto.class)))
    );
  }

  @Override
  public Mono<MovieInfoDto> getMovieById(String id) {
    return movieInfoRepository.findById(id).flatMap(movieInfo ->
        Mono.just((MovieInfoDto) conversionService.convert(movieInfo,
            TypeDescriptor.valueOf(MovieInfo.class),TypeDescriptor.valueOf(MovieInfoDto.class)))
    );
  }

  @Override
  public Mono<MovieInfoDto> updateMovie(MovieInfoDto movieInfoDto, String id) {
    MovieInfo updatedMovieInfo = conversionService.convert(movieInfoDto,MovieInfo.class);
    return movieInfoRepository.findById(id).flatMap(movieInfo -> {
      assert updatedMovieInfo != null;
      updatedMovieInfo.setMovieInfoId(movieInfo.getMovieInfoId());
      BeanUtils.copyProperties(updatedMovieInfo,movieInfo);
          return movieInfoRepository.save(movieInfo);
    }).flatMap(movieInfo ->
        Mono.just((MovieInfoDto) conversionService.convert(movieInfo,
            TypeDescriptor.valueOf(MovieInfo.class),TypeDescriptor.valueOf(MovieInfoDto.class)))
    );
  }

  @Override
  public Mono<Void> deleteMovie(String id) {
    return movieInfoRepository.deleteById(id);
  }

  @Override
  public Flux<MovieInfoDto> getAllMoviesInfoByYear(Integer year) {
    return movieInfoRepository.findByYear(year).flatMap(movieInfo ->
        Mono.just((MovieInfoDto) conversionService.convert(movieInfo,
            TypeDescriptor.valueOf(MovieInfo.class),TypeDescriptor.valueOf(MovieInfoDto.class)))
    );
  }
}

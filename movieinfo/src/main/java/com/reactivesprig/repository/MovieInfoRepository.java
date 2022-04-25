package com.reactivesprig.repository;

import com.reactivesprig.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo,String> {

  Mono<MovieInfo> findByName(String s);

  Flux<MovieInfo> findByYear(Integer year);
}

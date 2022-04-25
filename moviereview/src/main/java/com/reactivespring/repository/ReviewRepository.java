package com.reactivespring.repository;

import com.reactivespring.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository extends ReactiveMongoRepository<Review,String> {

  Mono<Void> deleteByMovieInfoId(String movieInfoId);
  Flux<Review> findByMovieInfoId(String movieInfoId);

}

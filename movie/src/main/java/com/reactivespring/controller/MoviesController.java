package com.reactivespring.controller;

import com.reactivespring.client.MovieInfoRestClient;
import com.reactivespring.client.MovieReviewRestClient;
import com.reactivespring.dto.MovieDto;
import com.reactivespring.dto.MovieInfoDto;
import com.reactivespring.dto.ReviewDto;
import com.reactivespring.service.KafkaProducerService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
@RequiredArgsConstructor
@Slf4j
public class MoviesController {

  private final MovieInfoRestClient movieInfoRestClient;
  private final MovieReviewRestClient movieReviewRestClient;
  private final KafkaProducerService producerService;

  @GetMapping("/{id}")
  public Mono<MovieDto> retrieveMovieById(@PathVariable("id") String movieId){
    return movieInfoRestClient.retriveMovieInfo(movieId)
                        .flatMap(movieInfoDto -> {
                          var reviewList = movieReviewRestClient.retriveMovieInfo(movieId).collectList();

                          return reviewList.map(reviewDtos -> {
                            var movieDto = new MovieDto(movieInfoDto,reviewDtos);
                            producerService.sendMessage(movieDto.toString());
                            return movieDto;
                          });
                        });
  }

  @GetMapping()
  public Flux<MovieDto> retrieveMovieWithReview(){
    return movieInfoRestClient.retriveMovieInfos()
        .flatMap(movieInfoDto -> {
          var reviewList = movieReviewRestClient.retriveMovieInfo(movieInfoDto.getMovieInfoId()).collectList();

          return reviewList.map(reviewDtos -> {
            var movieDto = new MovieDto(movieInfoDto,reviewDtos);
            producerService.sendMessage(movieDto.toString());
            return movieDto;
          });
        });

  }

  @GetMapping(value = "/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<MovieInfoDto> getMovieStream(){
    return movieInfoRestClient.retriveMovieInfoStream();

  }

  @GetMapping(value = "review/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ReviewDto> getMovieReviewStream(){
    return movieReviewRestClient.retriveMovieReviewStream();

  }

  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
  public Mono<String> movieServiceRateLimiterFallBack(Exception ex){
    log.error("Rate Limit Exceeded [{}]"+ex.getMessage());
    return Mono.just("Cannot accept request now.Please try after sometime");
  }
}

package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.dto.MovieReviewDto;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewRepository;

import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewHandler {

  private final Validator validator;

  private final ConversionService conversionService;

  private final ReviewRepository reviewRepository;

  Sinks.Many<Review> reviewSinkFromStart = Sinks.many().replay().all();

  Sinks.Many<Review> reviewSink = Sinks.many().multicast().onBackpressureBuffer();

  public Mono<ServerResponse> addReview(ServerRequest request) {
    return request.bodyToMono(MovieReviewDto.class)
        .doOnNext(this::validate)
        .map(movieReviewDto -> conversionService.convert(movieReviewDto, Review.class))
        .flatMap(reviewRepository::save)
        .doOnNext(review -> reviewSinkFromStart.tryEmitNext(review))
        .flatMap(review -> Mono.just(
            Objects.requireNonNull(getMovieReviewDto(review))))
        .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue).log();
  }

  private MovieReviewDto getMovieReviewDto(Review review) {
    log.info("Received review [{}]", review);
    MovieReviewDto movieReviewDto = conversionService.convert(review, MovieReviewDto.class);
    log.info("Converted dto [{}]", movieReviewDto);
    return movieReviewDto;
  }

  private void validate(MovieReviewDto review) {
    var contraintViolations = validator.validate(review);
    log.info("ConstrainViolations [{}]",contraintViolations);
    if(!contraintViolations.isEmpty()){
      var errorMessage = contraintViolations.stream()
          .map(ConstraintViolation::getMessage)
          .sorted()
          .collect(Collectors.joining(","));
      throw new ReviewDataException(errorMessage);
    }
  }

  public Mono<ServerResponse> getReviews(ServerRequest request) {
    var movieInfoId =  request.queryParam("movieInfoId");
    if(movieInfoId.isPresent()){
      var movieFlux = reviewRepository.findByMovieInfoId(movieInfoId.get());
      return getServerResponseMono(movieFlux);
    }else{
      var moviesFlux = reviewRepository.findAll();
      return getServerResponseMono(moviesFlux);
    }

  }

  public Mono<ServerResponse> updateReview(ServerRequest request) {
    var id = request.pathVariable("id");
    var existingReviewData = reviewRepository.findById(id)
                                          .switchIfEmpty(Mono.error(
                                          new ReviewNotFoundException("Review not found for "
                                              + "Review Id "+id)));
   return existingReviewData.flatMap(review -> request.bodyToMono(Review.class)
        .map(review1 -> {
          review.setComment(review1.getComment());
          review.setRating(review1.getRating());
          return review;
        }).flatMap(reviewRepository::save)
       .flatMap(savedReview->ServerResponse.ok().
           bodyValue(getMovieReviewDto(savedReview))));
  }

  public Mono<ServerResponse> deleteReview(ServerRequest request) {
    var id = request.pathVariable("id");
    var existingReviewData = reviewRepository.findById(id)
                                              .switchIfEmpty(Mono.error(
                                                  new ReviewNotFoundException("Review not found for "
                                                      + "Review Id "+id)));
    return existingReviewData.flatMap(review -> reviewRepository.deleteById(review.getReviewId()))
        .then(ServerResponse.status(HttpStatus.NO_CONTENT).build());

  }

  private Mono<ServerResponse> getServerResponseMono(Flux<Review> movieFlux) {
    var movieReviewDtoFlux = movieFlux.flatMap(review -> Mono.just(getMovieReviewDto(review)));
    return ServerResponse.ok().body(movieReviewDtoFlux, MovieReviewDto.class);
  }

  public Mono<ServerResponse> getReviewsStream(ServerRequest serverRequest) {
    log.info("Into streams endpoint [ {} ]",String.valueOf(serverRequest));
    return ServerResponse.ok().
        contentType(MediaType.TEXT_EVENT_STREAM).
        body(reviewSinkFromStart.asFlux(),Review.class)
        .log();
  }
}

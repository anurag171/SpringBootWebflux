package com.reactivespring.client;

import com.reactivespring.dto.ReviewDto;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import com.reactivespring.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MovieReviewRestClient {

  private final WebClient webClient;

  @Value("${restClient.movieReviewUrl}")
  private String movieReviewUrl;

  private static final String USER_RETRY_MSG = "Movie Service Down. Please try after sometime";

  public Flux<ReviewDto> retriveMovieInfo(String movieInfoId){

    var uri = UriComponentsBuilder
        .fromHttpUrl(movieReviewUrl)
        .queryParam("movieInfoId",movieInfoId)
        .buildAndExpand()
        .toUriString();

    return webClient.get()
        .uri(uri)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,clientResponse -> {
          if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
            return Mono.empty();
          }
          return clientResponse.bodyToMono(String.class)
              .flatMap(responseMsg -> Mono.error(new ReviewsClientException(responseMsg)));
        })
        .onStatus(HttpStatus::is5xxServerError,clientResponse ->
           clientResponse.bodyToMono(String.class)
              .flatMap(responseMsg -> Mono.error(new ReviewsServerException(USER_RETRY_MSG)))
        )
        .bodyToFlux(ReviewDto.class)
        .retryWhen(RetryUtil.retrySpec() )
        .log();
  }


  public Flux<ReviewDto> retriveMovieReviewStream() {
    var url = movieReviewUrl.concat("/stream");
    return webClient.get()
        .uri(url)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,clientResponse ->
            clientResponse.bodyToMono(String.class)
                .flatMap(responseMsg -> Mono.error(new MoviesInfoClientException(responseMsg,
                    clientResponse.rawStatusCode())))
        )
        .onStatus(HttpStatus::is5xxServerError,clientResponse ->
            clientResponse.bodyToMono(String.class)
                .flatMap(responseMsg -> Mono.error(new MoviesInfoServerException(USER_RETRY_MSG)))
        )
        .bodyToFlux(ReviewDto.class)
        //.retry(10)
        .retryWhen(RetryUtil.retrySpec())
        .log();
  }
}

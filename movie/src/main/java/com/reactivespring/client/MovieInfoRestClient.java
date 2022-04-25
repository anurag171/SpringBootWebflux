package com.reactivespring.client;

import com.reactivespring.dto.MovieInfoDto;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieInfoRestClient {

  private static final String SERVICE_NAME = "movieRestClientService";
  private static final String USER_RETRY_MSG = "Movie Service Down. Please try after sometime";

  private final WebClient webClient;

  @Value("${restClient.movieInfoUrl}")
  private String movieInfoUrl;

  @RateLimiter(name =SERVICE_NAME ,fallbackMethod = "movieServiceRateLimiterFallBack")
  public Flux<MovieInfoDto> retriveMovieInfos(){
    var url = movieInfoUrl;

    return webClient.get()
        .uri(url)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,clientResponse -> {
          if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
            return Mono.error(new MoviesInfoClientException("There is no movie Info available"
                ,clientResponse.statusCode().value()));
          }
          return clientResponse.bodyToMono(String.class)
              .flatMap(responseMsg -> Mono.error(new MoviesInfoClientException(responseMsg,
                  clientResponse.rawStatusCode())));
        })
        .onStatus(HttpStatus::is5xxServerError,clientResponse ->
            clientResponse.bodyToMono(String.class)
                .flatMap(responseMsg -> Mono.error(new MoviesInfoServerException(USER_RETRY_MSG)))
        )
        .bodyToFlux(MovieInfoDto.class)
        //.retry(10)
        .retryWhen(RetryUtil.retrySpec())
        .log();
  }


  public Mono<MovieInfoDto> retriveMovieInfo(String movieId){
    var url = movieInfoUrl.concat("/{id}");

    return webClient.get()
              .uri(url,movieId)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError,clientResponse -> {
            if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
              return Mono.error(new MoviesInfoClientException("There is no movie Info available for"
                  + " moviedId " + movieId,clientResponse.statusCode().value()));
            }
          return clientResponse.bodyToMono(String.class)
              .flatMap(responseMsg -> Mono.error(new MoviesInfoClientException(responseMsg,
                  clientResponse.rawStatusCode())));
        })
        .onStatus(HttpStatus::is5xxServerError,clientResponse ->
          clientResponse.bodyToMono(String.class)
              .flatMap(responseMsg -> Mono.error(new MoviesInfoServerException(USER_RETRY_MSG)))
        )
        .bodyToMono(MovieInfoDto.class)
        //.retry(10)
        .retryWhen(RetryUtil.retrySpec())
        .log();
  }

  public Flux<MovieInfoDto> retriveMovieInfoStream() {
    var url = movieInfoUrl.concat("/stream");
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
        .bodyToFlux(MovieInfoDto.class)
        //.retry(10)
        .retryWhen(RetryUtil.retrySpec())
        .log();

  }


  public Flux<String> movieServiceRateLimiterFallBack(@NonNull Exception ex){
    log.error("Rate Limit Exceeded [{}]"+ex.getMessage());
    return Flux.just("Cannot accept request now.Please try after sometime");
  }
}

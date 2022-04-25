package com.reactivespring.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.dto.MovieDto;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "36000")
@AutoConfigureWireMock(port = 8094)
@TestPropertySource(
    properties = {
        "restClient.movieInfoUrl=http://localhost:8094/v1/movieinfo",
        "restClient.movieReviewUrl=http://localhost:8094/v1/reviews"
    }
)
class MovieControllerIntgTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  void retrieveMovieById() {
    //given
    var movieId = "abc";
    stubFor(get(urlEqualTo("/v1/movieinfo" + "/" + movieId))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("movieinfo.json")));

    stubFor(get(urlPathEqualTo("/v1/reviews"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("reviews.json")));

    //when
    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(MovieDto.class)
        .consumeWith(movieEntityExchangeResult -> {
          var movie = movieEntityExchangeResult.getResponseBody();
          assert Objects.requireNonNull(movie).getReviewList().size() == 2;
          assertEquals("Batman Begins", movie.getMovieInfo().getName());
        });

  }

  @Test
  void retrieveMovieById_404() {
    //given
    var movieId = "abc";
    stubFor(get(urlEqualTo("/v1/movieinfo" + "/" + movieId))
        .willReturn(aResponse()
            .withStatus(404)
        ));

    stubFor(get(urlPathEqualTo("/v1/reviews"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("reviews.json")));

    //when
    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody(String.class)
        .isEqualTo("There is no movie Info available for moviedId abc");

    WireMock.verify(3,getRequestedFor(urlEqualTo("/v1/movieinfo/abc")));

  }

  @Test
  void retrieveMovieById_5XX() {
    //given
    var movieId = "abc1";
    stubFor(get(urlEqualTo("/v1/movieinfo" + "/" + movieId))
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("MovieInfo Service Unavailable")
        ));

    stubFor(get(urlPathEqualTo("/v1/reviews"))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("reviews.json")));

    //when
    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody(String.class)
        .isEqualTo("Movie Service Down. Please try after sometime");

   WireMock.verify(4,getRequestedFor(urlEqualTo("/v1/movieinfo/abc1")));

  }

  @Test
  void retrieveMovieById_reviews_404() {
    //given
    var movieId = "abc";
    stubFor(get(urlEqualTo("/v1/movieinfo" + "/" + movieId))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBodyFile("movieinfo.json")
        ));

    stubFor(get(urlPathEqualTo("/v1/reviews"))
        .willReturn(aResponse()
            .withStatus(404)));

    //when
    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(MovieDto.class)
        .consumeWith(movieEntityExchangeResult -> {
          var movie = movieEntityExchangeResult.getResponseBody();
          assert Objects.requireNonNull(movie).getReviewList().size() == 0;
          assertEquals("Batman Begins", movie.getMovieInfo().getName());
        });

  }
}

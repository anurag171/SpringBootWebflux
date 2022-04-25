package com.reactivesprig.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.reactivesprig.domain.MovieInfo;
import com.reactivesprig.dto.MovieInfoDto;
import com.reactivesprig.repository.MovieInfoRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
class MovieInfoControllerIntgTest {

  public static final String V_1_MOVIE_INFO = "/v1/movieinfo";
  @Autowired
  MovieInfoRepository movieInfoRepository;

  @Autowired
  WebTestClient webTestClient;

  @BeforeEach
  void setUp() {

    var movieInfoFlux = List.of(MovieInfo.builder().movieInfoId("1").name("Matrix").releaseDate(
            LocalDate.parse("2007-06-01", DateTimeFormatter.ISO_DATE)).cast(List.of("Keno Reves","Micheal")).year(2007).build(),
        new MovieInfo("2","Jurassic Park",1995,List.of("A","B"),LocalDate.parse("1995-06-01", DateTimeFormatter.ISO_DATE)),
        new MovieInfo("3","Harry Potter",2019,List.of("Toby Mcquire","DumbleDore"),LocalDate.parse("2019-06-01", DateTimeFormatter.ISO_DATE)));

    movieInfoRepository.saveAll(movieInfoFlux).blockLast();
  }

  @AfterEach
  void tearDown() {
    movieInfoRepository.deleteAll().block();
  }

  @Test
  void addMovieInfo() {

    webTestClient
        .post()
        .uri(V_1_MOVIE_INFO)
        .bodyValue(MovieInfo.builder().name("83").releaseDate(
                LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
            .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build())
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfoDto.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
          assert movieInfo != null;
          assert movieInfo.getMovieInfoId() != null;

        });
  }

  @Test
  void getAllMovies() {
   var movieInfoFlux =  webTestClient
                                              .get()
                                              .uri(V_1_MOVIE_INFO)
                                              .exchange()
                                              .expectStatus()
                                              .is2xxSuccessful()
                                              .returnResult(MovieInfo.class)
                                              .getResponseBody()
                                              .log();

    StepVerifier.create(movieInfoFlux).expectNextCount(3).verifyComplete();
  }

  @Test
  void getAllMovies_Approach2() {
    webTestClient
        .get()
        .uri(V_1_MOVIE_INFO)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(MovieInfo.class)
        .hasSize(3);
  }

  @Test
  void getAllMoviesByYear() {

   var uri= UriComponentsBuilder.fromUriString(V_1_MOVIE_INFO)
        .queryParam("year",2005).buildAndExpand().toUri();

    webTestClient
        .get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var response = movieInfoEntityExchangeResult.getResponseBody();
          assert response != null;
            response.forEach(movieInfo -> assertEquals(2005, movieInfo.getYear()));
        });

  }

  @Test
  void getMoviesById() {
    var id = "3";
    webTestClient
        .get()
        .uri(V_1_MOVIE_INFO+"/{id}",id)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var movieInfo =  movieInfoEntityExchangeResult.getResponseBody();
          assert movieInfo != null;
          assertNotNull(movieInfo.getMovieInfoId());
        });
  }

  @Test
  void getMoviesById_jsonPath() {
    var id = "3";
    webTestClient
        .get()
        .uri(V_1_MOVIE_INFO+"/{id}",id)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.name").isEqualTo("Harry Potter");
  }

  @Test
  void updateMovieInfo() {
    var id = "3";
    webTestClient
        .put()
        .uri(V_1_MOVIE_INFO+"/{id}",id)
        .bodyValue(MovieInfo.builder().name("83.5").releaseDate(
                LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
            .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build())
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var updatedMovieInfo =  movieInfoEntityExchangeResult.getResponseBody();
          assertNotNull(updatedMovieInfo);
          assertNotNull(updatedMovieInfo.getMovieInfoId());
          assertEquals("83.5",updatedMovieInfo.getName());
        });
  }

  @Test
  void updateMovieInfo_NotFound() {
    var id = "5";
    webTestClient
        .put()
        .uri(V_1_MOVIE_INFO+"/{id}",id)
        .bodyValue(MovieInfo.builder().name("83.5").releaseDate(
                LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
            .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build())
        .exchange()
        .expectStatus()
        .isNotFound()
        ;
  }

  @Test
  void deleteMovieInfo() {
    var id ="3";
    webTestClient
        .delete()
        .uri(V_1_MOVIE_INFO+"/{id}",id)
        .exchange()
        .expectStatus()
        .isNoContent()
        .expectBody(Void.class);
  }

  @Test
  void getAllMovies_stream() {

    webTestClient
        .post()
        .uri(V_1_MOVIE_INFO)
        .bodyValue(MovieInfo.builder().name("83").releaseDate(
                LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
            .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build())
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfoDto.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
          assert movieInfo != null;
          assert movieInfo.getMovieInfoId() != null;

        });

    var movieInfoFlux =  webTestClient
        .get()
        .uri(V_1_MOVIE_INFO+"/stream")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .returnResult(MovieInfo.class)
        .getResponseBody()
        .log();

    StepVerifier.create(movieInfoFlux)
        .assertNext(movieInfo -> {
          assert movieInfo.getMovieInfoId() != null;
        }).thenCancel()
        .verify();
  }
}
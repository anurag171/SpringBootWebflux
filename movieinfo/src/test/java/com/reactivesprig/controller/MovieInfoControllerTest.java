package com.reactivesprig.controller;

import static com.reactivesprig.controller.MovieInfoControllerIntgTest.V_1_MOVIE_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.reactivesprig.domain.MovieInfo;
import com.reactivesprig.dto.MovieInfoDto;
import com.reactivesprig.service.MovieInfoService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
class MovieInfoControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  private MovieInfoService movieInfoServiceMock;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void addMovieInfo() {

   MovieInfoDto movieInfoMock =MovieInfoDto.builder().movieInfoId("1").name("83").releaseDate(
            LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
        .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build();

    when(movieInfoServiceMock.addMovie(any())).thenReturn(Mono.just(movieInfoMock));
    webTestClient
        .post()
        .uri(V_1_MOVIE_INFO)
        .bodyValue(MovieInfo.builder().name("83").releaseDate(
                LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
            .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build())
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
          assert movieInfo != null;
          assert movieInfo.getMovieInfoId() != null;

        });
  }

  @Test
  void addMovieInfo_validate() {

    MovieInfoDto movieInfoMock =MovieInfoDto.builder().movieInfoId("1").name("").releaseDate(
            LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
        .cast(List.of("")).year(-2009).build();

    when(movieInfoServiceMock.addMovie(any())).thenReturn(Mono.just(movieInfoMock));
    webTestClient
        .post()
        .uri(V_1_MOVIE_INFO)
        .bodyValue(movieInfoMock)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(String.class)
        .consumeWith(stringEntityExchangeResult -> {
          var responseStr = stringEntityExchangeResult.getResponseBody();

          assert responseStr != null;
        });
  }

  @Test
  void getAllMovies() {
    var movieInfoFlux = List.of(MovieInfoDto.builder().movieInfoId("1").name("Matrix").releaseDate(
            LocalDate.parse("2007-06-01", DateTimeFormatter.ISO_DATE)).cast(List.of("Keno Reves","Micheal")).year(2007).build(),
        new MovieInfoDto("2","Jurassic Park",1995,List.of("A","B"),LocalDate.parse("1995-06-01", DateTimeFormatter.ISO_DATE)),
        new MovieInfoDto("3","Harry Potter",2019,List.of("Toby Mcquire","DumbleDore"),LocalDate.parse("2019-06-01", DateTimeFormatter.ISO_DATE)));


    when(movieInfoServiceMock.getAllMovies()).thenReturn(Flux.fromIterable(movieInfoFlux));
    webTestClient
        .get()
        .uri(V_1_MOVIE_INFO)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .returnResult(MovieInfo.class)
        .getResponseBody()
        .log();
  }

  @Test
  void getMoviesById() {
   var movieInfoMono = MovieInfoDto.builder().name("83.5").releaseDate(
            LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
        .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build();
    when(movieInfoServiceMock.getMovieById(isA(String.class))).thenReturn(Mono.just(movieInfoMono));
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
          assertEquals("83.5",movieInfo.getName());
        });
  }

  @Test
  void getMoviesById_NotFound() {
    var movieInfoMono = MovieInfo.builder().name("83.5").releaseDate(
            LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
        .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build();
    when(movieInfoServiceMock.getMovieById(isA(String.class))).thenReturn(Mono.empty());
    var id = "31";
    webTestClient
        .get()
        .uri(V_1_MOVIE_INFO+"/{id}",id)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void updateMovieInfo() {
    var movieInfoMono = MovieInfoDto.builder().movieInfoId("3").name("83.5").releaseDate(
            LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
        .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build();
    var id = "3";
    when(movieInfoServiceMock
            .updateMovie(isA(MovieInfoDto.class),isA(String.class)))
            .thenReturn(Mono.just(movieInfoMono));
    webTestClient
        .put()
        .uri(V_1_MOVIE_INFO+"/{id}",id)
        .bodyValue(movieInfoMono)
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
  void deleteMovieInfo() {
    var id = "abc";

    when(movieInfoServiceMock.deleteMovie(isA(String.class)))
        .thenReturn(Mono.empty());

    webTestClient
        .delete()
        .uri(V_1_MOVIE_INFO + "/{id}", id)
        .exchange()
        .expectStatus()
        .isNoContent();
  }
}
package com.reactivesprig.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.reactivesprig.domain.MovieInfo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@DataMongoTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles("test")
class MovieInfoRepositoryIT {

  @Autowired
  MovieInfoRepository movieInfoRepository;

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
  void findAll() {
   var flux= movieInfoRepository.findAll().log();

    StepVerifier.create(flux)
                .expectNextCount(3L)
                .verifyComplete();
  }

  @Test
  void findById() {
    var mono= movieInfoRepository.findById("3").log();

    StepVerifier.create(mono)
        .assertNext(movieInfo -> assertThat(movieInfo.getName()).isEqualTo("Harry Potter"))
        .verifyComplete();
  }

  @Test
  void saveMovie() {
    var mono= movieInfoRepository.save(MovieInfo.builder().name("83").releaseDate(
                        LocalDate.parse("2022-01-11", DateTimeFormatter.ISO_DATE))
                      .cast(List.of("Ranbir Singh","Deepika Padukone")).year(2022).build()).log();

    StepVerifier.create(mono)
        .assertNext(movieInfo -> {
          assertThat(movieInfo.getName()).isEqualTo("83");
          assertThat(movieInfo.getMovieInfoId()).isNotNull();
        })
        .verifyComplete();
  }

  @Test
  void updateMovie() {
    //var existingMovieInfo= movieInfoRepository.findById("3").block();
    var existingMovieInfo= movieInfoRepository.findByName("Harry Potter").block();

    assert existingMovieInfo != null;
    existingMovieInfo.setCast(List.of("Ranbir Singh", "Deepika Padukone", "Pankaj Tripathi"));
      var movieInfoUpdated = movieInfoRepository.save(existingMovieInfo).log();

    StepVerifier.create(movieInfoUpdated)
        .assertNext(movieInfo -> {
          assertThat(movieInfo.getMovieInfoId()).isNotNull();
          assertThat(movieInfo.getCast()).containsAll(List.of("Ranbir Singh","Deepika Padukone","Pankaj Tripathi"));
        })
        .verifyComplete();
  }

  @Test
  void deleteMovie() {
    var existingMovieInfo= movieInfoRepository.findById("3").block();
    assert existingMovieInfo != null;
    movieInfoRepository.delete(existingMovieInfo).block();
    var remainingMovie= movieInfoRepository.findAll().log();
    StepVerifier.create(remainingMovie)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void findByName() {
    var movieInfoFlux= movieInfoRepository.findByYear(2007).log();
    StepVerifier.create(movieInfoFlux)
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void findByYear() {
    var movieInfoFlux= movieInfoRepository.findByName("Matrix").log();
    StepVerifier.create(movieInfoFlux)
        .expectNextCount(1)
        .verifyComplete();
  }
}
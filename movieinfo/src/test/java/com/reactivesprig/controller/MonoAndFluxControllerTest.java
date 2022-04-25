package com.reactivesprig.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = MonoAndFluxController.class)
@AutoConfigureWebTestClient
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
class MonoAndFluxControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void stream() {

    var flux = webTestClient.get()
        .uri("/stream")
        .exchange().expectStatus()
        .is2xxSuccessful()
        .returnResult(Long.class).getResponseBody();

    StepVerifier.create(flux).expectNext(0L, 1L, 2L, 3L).thenCancel().verify();
  }

  @Test
  void flux_approach0() {
    var flux = webTestClient.get()
        .uri("/flux")
        .exchange().expectStatus()
        .is2xxSuccessful()
        .returnResult(Integer.class).getResponseBody();

    StepVerifier.create(flux).expectNext(1, 2, 3).thenCancel().verify();
  }

  @Test
  void flux_approach1() {
    var flux = webTestClient.get()
        .uri("/flux")
        .exchange().expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Integer.class)
        .hasSize(3)
        .contains(1,2,3);

    //StepVerifier.create(flux).expectNext(1L, 2L, 3L).thenCancel().verify();
  }

  @Test
  void flux_approach2() {
    var flux = webTestClient.get()
        .uri("/flux")
        .exchange().expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Integer.class)
        .hasSize(3)
        .consumeWith(listEntityExchangeResult -> {
          var responseBody = listEntityExchangeResult.getResponseBody();
          assert Objects.requireNonNull(responseBody).size() ==3;
        });

    //StepVerifier.create(flux).expectNext(1L, 2L, 3L).thenCancel().verify();
  }
}
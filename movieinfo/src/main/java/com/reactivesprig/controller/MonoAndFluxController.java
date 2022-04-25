package com.reactivesprig.controller;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class MonoAndFluxController {

  @GetMapping(value = "/flux")
  public Flux<Integer> flux() {
    return Flux.just(1,2,3);
  }

  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Long> stream() {
    return Flux.interval(Duration.of(1, ChronoUnit.SECONDS)).log();
  }

}

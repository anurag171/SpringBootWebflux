package com.reactivesprig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;

class SinkTest {

  @Test
  void sink_multicast() {
    Sinks.Many<Integer> multicast = Sinks.many().multicast().onBackpressureBuffer();
    multicast.emitNext(1, EmitFailureHandler.FAIL_FAST);
    multicast.emitNext(2, EmitFailureHandler.FAIL_FAST);

    multicast.asFlux().subscribe(integer -> System.out.println("Subsriber 1 " + integer));
    multicast.asFlux().subscribe(integer -> System.out.println("Subsriber 2 " + integer));

    multicast.emitNext(3, EmitFailureHandler.FAIL_FAST);
    Assertions.assertNotNull(multicast);
  }

  @Test
  void sink_unicast() {
    {
      Sinks.Many<Integer> unicast = Sinks.many().unicast().onBackpressureBuffer();
      unicast.emitNext(1, EmitFailureHandler.FAIL_FAST);
      unicast.emitNext(2, EmitFailureHandler.FAIL_FAST);

      unicast.asFlux().subscribe(integer -> System.out.println("Subsriber 1 " + integer));
      //unicast.asFlux().subscribe(integer -> System.out.println("Subsriber 2 " + integer));

      unicast.emitNext(3, EmitFailureHandler.FAIL_FAST);
      Assertions.assertNotNull(unicast);
    }
  }

  @Test
  void sinkTest() {

    Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

    replaySink.emitNext(1, EmitFailureHandler.FAIL_FAST);
    replaySink.emitNext(2, EmitFailureHandler.FAIL_FAST);
    replaySink.emitNext(3, EmitFailureHandler.FAIL_FAST);
    replaySink.emitNext(4, EmitFailureHandler.FAIL_FAST);

    replaySink.asFlux().subscribe(integer -> System.out.println("Subsriber 1 " + integer));
    replaySink.asFlux().subscribe(integer -> System.out.println("Subsriber 2 " + integer));

    replaySink.emitNext(5, EmitFailureHandler.FAIL_FAST);
    int i = 0;
    while ( i<1000){
      replaySink.tryEmitNext(i);
      i++;
    }

    Assertions.assertNotNull(replaySink);

  }
}

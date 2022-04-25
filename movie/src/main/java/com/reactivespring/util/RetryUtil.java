package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RetryUtil {

  public static Retry retrySpec(){
    return Retry.fixedDelay(3, Duration.ofSeconds(5))
        .filter(MoviesInfoServerException.class::isInstance)
        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
  }

}

package com.reactivespring.exceptionhandler;

import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import java.net.ConnectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleRequestBodyError(WebExchangeBindException ex) {
        log.error("Exception Caught in handleRequestBodyError : {} ", ex.getMessage(), ex);
        var error = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted()
                .collect(Collectors.joining(","));
        log.error("Error is : {} ", error);
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handleMoviesInfoClientException(MoviesInfoClientException ex) {
        log.error("Exception Caught in handleMoviesInfoClientException : {} ", ex.getMessage(), ex);
        return  ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());

    }
    @ExceptionHandler(ReviewsClientException.class)
    public ResponseEntity<String> handleReviewsClientException(ReviewsClientException ex) {
        log.error("Exception Caught in handleReviewsClientException : {} ", ex.getMessage(), ex);
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

    }
    @ExceptionHandler(MoviesInfoServerException.class)
    public ResponseEntity<String> handleMoviesInfoServerException(MoviesInfoServerException ex) {
        log.error("Exception Caught in handleMoviesInfoServerException : {} ", ex.getMessage(), ex);
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());

    }
    @ExceptionHandler(ReviewsServerException.class)
    public ResponseEntity<String> handleReviewsServerException(ReviewsServerException ex) {
        log.error("Exception Caught in handleReviewsServerException : {} ", ex.getMessage(), ex);
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());

    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Exception Caught in handleGenericException : {} ", ex.getMessage(), ex);
        return  ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage()+" .Please try after sometime");

    }
}

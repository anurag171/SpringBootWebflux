package com.reactivespring.exception;

public class MoviesInfoServerException extends RuntimeException{
    private final String message;

    public MoviesInfoServerException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

package com.reactivespring.exception;

public class ReviewsServerException extends RuntimeException{

    @Override
    public String getMessage() {
        return message;
    }

    private final String message;

    public ReviewsServerException(String message) {
        super(message);
        this.message = message;
    }
}

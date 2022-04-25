package com.reactivespring.exception;

public class ReviewsClientException extends RuntimeException{
    private final String message;

    public ReviewsClientException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * get field
     *
     * @return message
     */
    @Override
    public String getMessage() {
        return this.message;
    }
}

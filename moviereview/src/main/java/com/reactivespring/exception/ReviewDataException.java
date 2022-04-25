package com.reactivespring.exception;

public class ReviewDataException extends RuntimeException{
  private final String message;
  public ReviewDataException(String message){
    super(message);
    this.message=message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}

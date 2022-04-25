package com.reactivespring.exception;

public class ReviewNotFoundException extends RuntimeException{
  private final String message;
  @lombok.Getter
  private Throwable ex;
  public ReviewNotFoundException(String message,Throwable ex){
    super(message);
    this.message=message;
    this.ex = ex;
  }

  public ReviewNotFoundException(String message){
    super(message);
    this.message=message;
  }

  @Override
  public String getMessage() {
    return message;
  }

}

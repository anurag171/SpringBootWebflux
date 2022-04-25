package com.reactivespring.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

  @Bean
  public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler){
    return route()
        .nest(path("/v1/reviews"),builder ->
          builder.POST("",reviewHandler::addReview)
                 .GET("", reviewHandler::getReviews)
              .nest(path("/{id}"),builder1 ->
                  builder1.PUT("",reviewHandler::updateReview)
                      .DELETE("",reviewHandler::deleteReview))
              .nest(path("/stream"),builder1 ->
                  builder1.GET("",reviewHandler::getReviewsStream))
        )
        .GET("/v1/helloworld",(request -> ServerResponse.ok().bodyValue("Hello World!")))
        .build();
  }
}

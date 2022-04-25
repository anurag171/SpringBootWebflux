package com.reactivespring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.reactivespring.convertor.MovieReviewToMovieReviewDtoConvertor;
import com.reactivespring.domain.Review;
import com.reactivespring.dto.MovieReviewDto;
import com.reactivespring.exceptionhandler.GlobalExceptionHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewRepository;
import com.reactivespring.router.ReviewRouter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalExceptionHandler.class})
@AutoConfigureWebTestClient
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
class MovieReviewUnitTests {

  @MockBean
  private ReviewRepository reviewRepository;

  /*@MockBean
  private ConversionService conversionService;*/

  @Autowired
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {

  }

  @Test
  void getReviews() {

    var reviewList =  List.of(
        new Review(null, "1", "Awesome Movie", 9.0),
        new Review(null, "1", "Awesome Movie1", 9.0),
        new Review(null, "2", "Excellent Movie", 8.0));
    //given
    when(reviewRepository.findAll()).thenReturn(Flux.fromIterable(reviewList));
    /*when(conversionService.convert(any(Review.class), MovieReviewDto.class))
        .thenReturn(new MovieReviewDto());*/
    //when
    webTestClient
        .get()
        .uri("/v1/reviews")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Review.class)
        .value(reviews -> assertEquals(3, reviews.size()));

  }

  @Test
  void getReviewsByMovieInfoId() {
    //given
    var reviewList1 =  List.of(
        new Review(null, "1", "Awesome Movie", 9.0),
        new Review(null, "1", "Awesome Movie1", 9.0)
        );
    //given
    when(reviewRepository.findByMovieInfoId(anyString())).thenReturn(Flux.fromIterable(reviewList1));
    //when
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/v1/reviews")
            .queryParam("movieInfoId", "1")
            .build())
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBodyList(Review.class)
        .value(reviewList -> {
          System.out.println("reviewList : " + reviewList);
          assertEquals(2, reviewList.size());
        });

  }

  @Test
  void addReview() {

    //given
    var review = new Review(null, "1", "Awesome Movie", 9.0);
    when(reviewRepository.save(isA(Review.class)))
        .thenReturn(Mono.just(
            new Review("abc", "1",
                "Awesome Movie", 9.0)));
    //when
    webTestClient
        .post()
        .uri("/v1/reviews")
        .bodyValue(review)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Review.class)
        .consumeWith(reviewResponse -> {
          var savedReview = reviewResponse.getResponseBody();
          assert savedReview != null;
          assertNotNull(savedReview.getReviewId());
        });


  }

  @Test
  void addReview_validations() {

    //given
    var review = new Review(null, null, "Awesome Movie", -9.0);
    when(reviewRepository.save(isA(Review.class)))
        .thenReturn(Mono.just(
            new Review("abc", "1",
                "Awesome Movie", 9.0)));
    //when
    webTestClient
        .post()
        .uri("/v1/reviews")
        .bodyValue(review)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody(String.class)
        .isEqualTo("review.movieInfoId: must not be null,review.rating: please pass a non negative value");


  }

  @Test
  void updateReview() {
    //given

    var reviewUpdate = new Review(null, "1", "Not an Awesome Movie", 8.0);

    when(reviewRepository.save(isA(Review.class))).thenReturn(Mono.just(new Review("abc", "1", "Not an Awesome Movie", 8.0)));
    when(reviewRepository.findById((String) any())).thenReturn(Mono.just(new Review("abc", "1", "Awesome Movie", 9.0)));
    //when


    webTestClient
        .put()
        .uri("/v1/reviews/{id}", "abc")
        .bodyValue(reviewUpdate)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Review.class)
        .consumeWith(reviewResponse ->{
          var updatedReview = reviewResponse.getResponseBody();
          assert updatedReview != null;
          System.out.println("updatedReview : "+ updatedReview);
          assertEquals(8.0,updatedReview.getRating());
          assertEquals("Not an Awesome Movie", updatedReview.getComment());
        });

  }

  @Test
  void updateReview_validation_notfound() {
    //given

    var reviewUpdate = new Review(null, "1", "Not an Awesome Movie", 8.0);

    when(reviewRepository.save(isA(Review.class))).thenReturn(Mono.just(new Review("abc", "1", "Not an Awesome Movie", 8.0)));
    when(reviewRepository.findById((String) any())).thenReturn(Mono.just(new Review("abc", "1", "Awesome Movie", 9.0)));
    //when


    webTestClient
        .put()
        .uri("/v1/reviews/{id}", "abc1")
        .bodyValue(reviewUpdate)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Review.class)
        .consumeWith(reviewResponse ->{
          var updatedReview = reviewResponse.getResponseBody();
          assert updatedReview != null;
          System.out.println("updatedReview : "+ updatedReview);
          assertEquals(8.0,updatedReview.getRating());
          assertEquals("Not an Awesome Movie", updatedReview.getComment());
        });

  }
}

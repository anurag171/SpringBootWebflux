package com.reactivespring.router;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.reactivespring.domain.Review;
import com.reactivespring.dto.MovieReviewDto;
import com.reactivespring.repository.ReviewRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "60000000")
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {

        var reviewsList = List.of(
                new Review(null, "1", "Awesome Movie", 9.0),
                new Review(null, "1", "Awesome Movie1", 9.0),
                new Review(null, "2", "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll()
                .block();
    }

    @Test
    void name() {
        //given

        //when
        webTestClient
                .get()
                .uri("/v1/helloworld")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("Hello World!");
    }

    @Test
    void getReviews() {
        //given

        //when
        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieReviewDto.class)
                .value(reviews -> assertEquals(3, reviews.size()));

    }

    @Test
    void getReviewsByMovieInfoId() {
        //given

        //when
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/v1/reviews")
                        .queryParam("movieInfoId", "1")
                        .build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieReviewDto.class)
                .value(reviewList -> {
                    System.out.println("reviewList : " + reviewList);
                    assertEquals(2, reviewList.size());
                });

    }

    @Test
    void addReview() {
        //given
        var review = new Review(null, "1", "Awesome Movie", 9.0);
        //when
        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieReviewDto.class)
                .consumeWith(reviewResponse -> {
                    var savedReview = reviewResponse.getResponseBody();
                    assert savedReview != null;
                    assertNotNull(savedReview.getReviewId());
                });

    }

    @Test
    void updateReview() {
        //given
        var review = new Review(null, "1", "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        var reviewUpdate = new Review(null, "1", "Not an Awesome Movie", 8.0);
        //when
        assert savedReview != null;

        webTestClient
                .put()
                .uri("/v1/reviews/{id}", savedReview.getReviewId())
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieReviewDto.class)
                .consumeWith(reviewResponse -> {
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    System.out.println("updatedReview : " + updatedReview);
                    assertNotNull(savedReview.getReviewId());
                    assertEquals(8.0, updatedReview.getRating());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });

    }

    @Test
    void updateReview_NotFound() {
        //given
        var reviewUpdate = new Review(null, "1", "Not an Awesome Movie", 8.0);
        //when
        webTestClient
                .put()
                .uri("/v1/reviews/{id}", "abc")
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteReview() {
        //given
        var review = new Review(null, "1", "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        //when
        assert savedReview != null;
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", savedReview.getReviewId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteReview_notFound() {
        //given
        //when
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", "123")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void error() {
        //given

        //when

        webTestClient
                .get()
                .uri("/v1/error")
                .exchange()
                .expectStatus()
                .is4xxClientError();

    }


}
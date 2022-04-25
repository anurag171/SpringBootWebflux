package com.reactivespring.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {

  @Id
  private String reviewId;
  @NotNull(message = "review.movieInfoId: must not be null" )
  private String movieInfoId;
  @Length(min = 5,max=100,message = "review.comment: cannot be more less than  5 and more than 100 ")
  private String comment;
  @Min(value = 0L,message = "review.rating: please pass a non negative value")
  private Double rating;

}

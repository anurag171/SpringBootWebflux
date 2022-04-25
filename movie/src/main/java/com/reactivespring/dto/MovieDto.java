package com.reactivespring.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class MovieDto {

  private MovieInfoDto movieInfo;
  private List<ReviewDto> reviewList;
}

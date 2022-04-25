package com.reactivespring.dto;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieInfoDto {
  private String movieInfoId;
  @NotBlank(message = "movieInfo.name must be present")
  private String name;
  @NotNull(message = "movieInfo.year must be present")
  @Positive(message = "movieInfo.year must be positive value")
  private Integer year;
  private List<@NotBlank(message = "movieInfo.cast must be present") String> cast;
  private LocalDate releaseDate;
}

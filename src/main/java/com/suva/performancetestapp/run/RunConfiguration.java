package com.suva.performancetestapp.run;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunConfiguration {
  @NotNull
  private Integer numberOfThreads = 2;
  @NotNull
  private Long delay= 100L;
  @NotNull
  private Integer numberOfMessages = 100;
}

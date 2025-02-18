package com.suva.performancetestapp.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunStateJson {
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private boolean running;
  private int processedMessages;
  private int failedMessages;
  private int successMessages;
  private int waitingMessages;
  private long minimumProcessingTime;
  private long averageProcessingTime;
  private long maximumProcessingTime;

  public String getElapsedTime() {
    if (startTime == null) {
      return "00:00";
    }

    Duration duration = endTime == null ? Duration.between(startTime, LocalDateTime.now())
        : Duration.between(startTime, endTime);

    return String.format("%02d:%02d", duration.toMinutes(), duration.toSecondsPart());
  }

}

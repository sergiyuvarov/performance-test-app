package com.suva.performancetestapp.run;

import com.suva.performancetestapp.feedback.FeedbackMessage;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestMessage {
  private String correlationId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Duration duration;
  private FeedbackMessage.FeedbackStatus status;
}

package com.suva.performancetestapp.feedback;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackMessage {
  public enum FeedbackStatus {OK, NOK}

  private String correlationId;
  private FeedbackStatus status;
}

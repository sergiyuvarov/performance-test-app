package com.suva.performancetestapp.run;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suva.performancetestapp.feedback.FeedbackMessage;
import com.suva.performancetestapp.feedback.FeedbackMessage.FeedbackStatus;
import com.suva.performancetestapp.ui.RunStateJson;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunState {

  @Getter
  private RunConfiguration configuration;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  @Getter
  private boolean running;
  @Getter
  private AtomicInteger processedMessages = new AtomicInteger(0);
  private AtomicInteger failedMessages = new AtomicInteger(0);
  private AtomicInteger successMessages = new AtomicInteger(0);
  private AtomicInteger waitingMessages = new AtomicInteger(0);
  private long minimumProcessingTime;
  private long averageProcessingTime;
  private long maximumProcessingTime;

  private Map<String, RequestMessage> messages = new ConcurrentHashMap<>();

  public void newState(RunConfiguration configuration) {
    this.configuration = configuration;
    startTime = LocalDateTime.now();
    messages = new ConcurrentHashMap<>();
    endTime = null;
    running = true;

    processedMessages = new AtomicInteger(0);
    failedMessages = new AtomicInteger(0);
    successMessages = new AtomicInteger(0);
    waitingMessages = new AtomicInteger(0);

    minimumProcessingTime = 0L;
    averageProcessingTime = 0L;
    maximumProcessingTime = 0L;
  }

  public void stop() {
    endTime = LocalDateTime.now();
    running = false;
  }

  public void incrementProcessedMessages() {
    processedMessages.incrementAndGet();
  }

  public RequestMessage createRequestMessage() {
    RequestMessage message = new RequestMessage();
    message.setCorrelationId(UUID.randomUUID().toString());
    message.setStartTime(LocalDateTime.now());

    messages.put(message.getCorrelationId(), message);
    return message;
  }

  public void applyFeedback(FeedbackMessage feedbackMessage) {
    RequestMessage message = messages.get(feedbackMessage.getCorrelationId());
    if (message == null) {
      log.error("No message found for correlationId: {}", feedbackMessage.getCorrelationId());
    }

    message.setEndTime(LocalDateTime.now());
    message.setDuration(Duration.between(message.getStartTime(), message.getEndTime()));

    long duration = message.getDuration().toMillis();
    if (duration > maximumProcessingTime) {
      maximumProcessingTime = duration;
    }
    if (duration < minimumProcessingTime || minimumProcessingTime == 0L) {
      minimumProcessingTime = duration;
    }

    message.setStatus(feedbackMessage.getStatus());
    if (feedbackMessage.getStatus() == FeedbackStatus.OK) {
      successMessages.incrementAndGet();
    } else {
      failedMessages.incrementAndGet();
    }
    processedMessages.incrementAndGet();
  }

  public RunStateJson toJson() {
    calculateAverageProcessingTime();

    RunStateJson json = new RunStateJson();
    json.setStartTime(startTime);
    json.setEndTime(endTime);
    json.setRunning(running);
    json.setProcessedMessages(processedMessages.get());
    json.setFailedMessages(failedMessages.get());
    json.setSuccessMessages(successMessages.get());
    json.setWaitingMessages(waitingMessages.get());
    json.setMinimumProcessingTime(minimumProcessingTime);
    json.setAverageProcessingTime(averageProcessingTime);
    json.setMaximumProcessingTime(maximumProcessingTime);
    return json;
  }

  private void calculateAverageProcessingTime() {
    long value = 0L;
    int count = 0;
    for (RequestMessage message : messages.values()) {
      if (message.getDuration() != null) {
        value += message.getDuration().toMillis();
        count++;
      }
    }
    averageProcessingTime = count > 0 ? value / count : 0;
  }
}

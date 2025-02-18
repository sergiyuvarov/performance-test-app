package com.suva.performancetestapp.test;

import com.suva.performancetestapp.feedback.FeedbackMessage;
import com.suva.performancetestapp.feedback.FeedbackMessage.FeedbackStatus;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/test")
public class TestController {
  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
  private final RestClient restClient = RestClient.builder()
      .baseUrl("http://localhost:8080/feedback")
      .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
      .build();

  @PostMapping(path = "message", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> message(@RequestBody JsonMessage message) {

    executor.schedule(() -> sendResponse(message), 500, TimeUnit.MILLISECONDS);
    return ResponseEntity.ok().build();
  }

  private void sendResponse(JsonMessage message) {
    FeedbackMessage feedbackMessage = new FeedbackMessage();
    feedbackMessage.setCorrelationId(message.getCorrelationId());
    feedbackMessage.setStatus(FeedbackStatus.OK);
    restClient.post().body(feedbackMessage).retrieve().toBodilessEntity();
  }
}

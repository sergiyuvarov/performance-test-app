package com.suva.performancetestapp.feedback;

import com.suva.performancetestapp.run.RunService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedbackController {
  private final RunService runService;

  @PostMapping(value = "/feedback", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void feedback(@RequestBody FeedbackMessage feedbackMessage) {
    runService.applyFeedback(feedbackMessage);
  }
}

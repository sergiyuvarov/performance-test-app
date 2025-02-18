package com.suva.performancetestapp.run;

import com.suva.performancetestapp.feedback.FeedbackMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class RunService {
  private final RestClient restClient;
  private final RunState runState = new RunState();
  private List<Thread> threads;

  public void startProcessing(RunConfiguration configuration) {
    runState.newState(configuration);

    threads = new ArrayList<>();
    for (int i = 0; i < configuration.getNumberOfThreads(); i++) {
      Thread thread = new Thread(new Worker(runState, restClient));
      threads.add(thread);
      thread.start();
    }

    Thread thread = new Thread(waitingThreads());
    thread.start();
  }

  private Runnable waitingThreads() {
    return () -> {
      for (Thread thread : threads) {
        try {
          thread.join();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      runState.stop();
    };
  }

  public void stopProcessing() {
    runState.stop();
  }

  public void applyFeedback(FeedbackMessage feedbackMessage) {
    runState.applyFeedback(feedbackMessage);
    log.info("Received feedback message: {}, {}", feedbackMessage.getCorrelationId(), feedbackMessage.getStatus());
  }
}

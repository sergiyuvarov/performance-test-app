package com.suva.performancetestapp.run;

import com.suva.performancetestapp.test.JsonMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
public class Worker implements Runnable {

  private final RunState runState;
  private final RestClient restClient;

  public Worker(RunState runState, RestClient restClient) {
    this.runState = runState;
    this.restClient = restClient;
  }

  @Override
  public void run() {
    for (int i = 0; i < runState.getConfiguration().getNumberOfMessages(); i++) {

      RequestMessage requestMessage = runState.createRequestMessage();
      JsonMessage jsonMessage = new JsonMessage(requestMessage.getCorrelationId());
      restClient.post().body(jsonMessage).retrieve()
          .onStatus(HttpStatusCode::isError, (request, response) ->
              log.error("Error while posting json message, status: {}, url: {} ",
                  response.getStatusCode(), request.getURI()))
          .toBodilessEntity();

      runState.incrementProcessedMessages();
      log.info("Send message {} : {}", runState.getProcessedMessages(),
          requestMessage.getCorrelationId());

      try {
        Thread.sleep(runState.getConfiguration().getDelay());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      if (!runState.isRunning()) {
        return;
      }
    }
  }
}

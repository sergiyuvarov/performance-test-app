package com.suva.performancetestapp.ui;

import com.suva.performancetestapp.run.RunConfiguration;
import com.suva.performancetestapp.run.RunService;
import com.suva.performancetestapp.run.RunState;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UIController {
  private final RunService runService;

  @GetMapping("/")
  public String index(Model model) {
    return runService.getRunState().isRunning() ? "redirect:/runState" : "redirect:/newRun";
  }

  @GetMapping("/runState")
  public String runState(Model model) {
    model.addAttribute("state", runService.getRunState());
    return "runState";
  }

  @GetMapping("/newRun")
  public String newRun(Model model) {
    model.addAttribute("configuration", new RunConfiguration());
    return "newRun";
  }

  @PostMapping("/run")
  public String run(@ModelAttribute("configuration") @Valid RunConfiguration configuration, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "newRun";
    }

    runService.startProcessing(configuration);
    return "redirect:runState";
  }

  @PostMapping("/stop")
  public String stop(Model model) {
    runService.stopProcessing();
    model.addAttribute("state", runService.getRunState());
    return "runState";
  }

  @PostMapping("/newRun")
  public String newRun() {
   return "redirect:/newRun";
  }

  @GetMapping(path = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RunStateJson> getStatus() {
    return ResponseEntity.ok(runService.getRunState().toJson());
  }
}

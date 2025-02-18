package com.suva.performancetestapp.ui;

import com.suva.performancetestapp.run.RunConfiguration;
import com.suva.performancetestapp.run.RunService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UIController {

  public static final String STATE_ATTR = "state";
  private final RunService runService;

  @GetMapping("/")
  public String index() {
    return runService.getRunState().isRunning() ? "redirect:/runState" : "redirect:/newRun";
  }

  @GetMapping("/runState")
  public String runState(Model model) {
    model.addAttribute(STATE_ATTR, runService.getRunState().toJson());
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
    model.addAttribute(STATE_ATTR, runService.getRunState().toJson());
    return "runState";
  }

  @PostMapping("/newRun")
  public String newRun() {
   return "redirect:/newRun";
  }

  @GetMapping(path = "/status")
  public String getStatus2(Model model) {
    model.addAttribute(STATE_ATTR, runService.getRunState().toJson());
    return "runState :: #table";
  }
}

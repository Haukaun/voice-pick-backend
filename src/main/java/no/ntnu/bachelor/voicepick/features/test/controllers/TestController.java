package no.ntnu.bachelor.voicepick.features.test.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for testing role based authenticaion
 * 
 * @author Joakim
 */
@RestController
public class TestController {

  @GetMapping("/version")
  public String version() {
    return "Version 0.1";
  }

  @GetMapping("/user")
  public String user() {
    return "You have the role USER";
  }

  @GetMapping("/leader")
  public String leader() {
    return "You have the role leader";
  }

  // @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin")
  public String admin() {
    return "You have the role admin";
  }

}
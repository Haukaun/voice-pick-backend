package no.ntnu.bachelor.voicepick.features.test.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<String> version() {
    return new ResponseEntity<>("Version 0.1", HttpStatus.OK);
  }

  @GetMapping("/user")
  public ResponseEntity<String> user() {
    return new ResponseEntity<>("You have the role USER", HttpStatus.OK);
  }

  @GetMapping("/leader")
  public ResponseEntity<String> leader() {
    return new ResponseEntity<>("You have the role leader", HttpStatus.OK);
  }

  // @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin")
  public ResponseEntity<String> admin() {
    return new ResponseEntity<>("You have the role admin", HttpStatus.OK);
  }

}
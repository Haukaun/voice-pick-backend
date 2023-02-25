package no.ntnu.bachelor.voicepick.features.authentication.dtos;

import lombok.Data;

@Data
public class SignupRequest {
  private String email;
  private String password;
  private String firstName;
  private String lastName;
}

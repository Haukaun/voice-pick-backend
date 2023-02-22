package no.ntnu.bachelor.voicepick.dtos;

import lombok.Data;

/**
 * A class containing information about an location
 * 
 * @author Joakim
 */
@Data
public class AddLocationRequest {
  private String location;
  private String controlDigits;
}

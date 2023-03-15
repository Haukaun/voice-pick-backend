package no.ntnu.bachelor.voicepick.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A class containing information about an location
 * 
 * @author Joakim
 */
@Data
@AllArgsConstructor
public class AddLocationRequest {
  private String name;
  private int controlDigits;
}

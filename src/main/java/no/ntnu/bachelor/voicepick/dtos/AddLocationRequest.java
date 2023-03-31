package no.ntnu.bachelor.voicepick.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class containing information about an location
 * 
 * @author Joakim
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddLocationRequest {
  private String code;
  private int controlDigits;
}

package no.ntnu.bachelor.voicepick.features.pluck.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdatePluckRequest {
    private int amountPlucked;
    private LocalDateTime confirmedAt;
    private LocalDateTime pluckedAt;
}

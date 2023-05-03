package no.ntnu.bachelor.voicepick.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationPluckListResponse {

    private Long id;
    private String route;
    private String destination;

}

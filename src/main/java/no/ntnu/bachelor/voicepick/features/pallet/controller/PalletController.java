package no.ntnu.bachelor.voicepick.features.pallet.controller;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.features.pallet.dtos.PalletInfoDto;
import no.ntnu.bachelor.voicepick.features.pallet.service.PalletService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used to operate product information
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class PalletController {

  private final PalletService palletService;
  private final ModelMapper modelMapper;

  @GetMapping("/{gtin}")
  public ResponseEntity<PalletInfoDto> getProductInfo(@PathVariable String gtin) {
    var result = this.palletService.findByGtin(gtin);

    ResponseEntity<PalletInfoDto> response;
    if (result.isEmpty()) {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      var dto = this.modelMapper.map(result.get(), PalletInfoDto.class);
      response = new ResponseEntity<>(dto, HttpStatus.OK);
    }

    return response;
  }
}

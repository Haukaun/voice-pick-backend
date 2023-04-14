package no.ntnu.bachelor.voicepick.features.pallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ntnu.bachelor.voicepick.features.pallet.models.PalletInfo;
import no.ntnu.bachelor.voicepick.features.pallet.service.PalletService;
import no.ntnu.bachelor.voicepick.models.ProductType;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Initializes some product info dummy data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PalletDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

  private final PalletService palletService;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (palletService.getAll().isEmpty()) {
      log.info("Adding dummy scanner data...");

      palletService.addPalletInfo("5060896625638", new PalletInfo(
              "Monster Energy - Lewis Hamilton",
              0.5,
              0.5,
              200,
              ProductType.F_PAK
      ));
      palletService.addPalletInfo("7021110120818", new PalletInfo(
              "Antibakk",
              0.5,
              0.5,
              150,
              ProductType.F_PAK
      ));

      log.info("Finished adding dummy scanner data!");
    } else {
      log.info("Dummy scanner data already added. Skipping this step...");
    }
  }
}

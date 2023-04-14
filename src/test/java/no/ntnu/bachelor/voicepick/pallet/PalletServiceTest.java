package no.ntnu.bachelor.voicepick.pallet;

import jakarta.persistence.EntityExistsException;
import no.ntnu.bachelor.voicepick.features.pallet.models.PalletInfo;
import no.ntnu.bachelor.voicepick.features.pallet.service.PalletService;
import no.ntnu.bachelor.voicepick.models.ProductType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PalletServiceTest {

  @Autowired
  private PalletService palletService;

  @BeforeEach
  void setup() {
    this.palletService.emptyList();
  }

  @AfterEach
  void teardown() {
    this.palletService.emptyList();
  }

  @Test
  @DisplayName("Create pallet info")
  void createPalletInfo() {
    var palletInfo = new PalletInfo("product1", 1.0, 2.0, 10, ProductType.D_PAK);

    assertEquals("product1", palletInfo.getProductName());
    assertEquals(1.0, palletInfo.getProductWeight());
    assertEquals(2.0, palletInfo.getProductVolume());
    assertEquals(10, palletInfo.getQuantity());
    assertEquals(ProductType.D_PAK, palletInfo.getType());
  }

  @Test
  @DisplayName("Add product info")
  void addProductInfo() {
    this.palletService.addPalletInfo(
            "123",
            new PalletInfo(
                    "product1",
                    1.0,
                    1.0,
                    10,
                    ProductType.D_PAK
            )
    );

    assertEquals(1, this.palletService.getAll().size());
  }

  @Test
  @DisplayName("Add duplicate product info")
  void addDuplicateProductInfo() {
    this.palletService.addPalletInfo(
            "123",
            new PalletInfo(
                    "product1",
                    1.0,
                    1.0,
                    10,
                    ProductType.D_PAK
            )
    );

    try {
      this.palletService.addPalletInfo(
              "123",
              new PalletInfo(
                      "product1",
                      1.0,
                      1.0,
                      10,
                      ProductType.D_PAK
              )
      );
      fail("EntityExistsException was not thrown");
    } catch (EntityExistsException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Get all entries when there are none")
  void getAllEntriesWhenEmpty() {
    assertEquals(0, this.palletService.getAll().size());
  }

  @Test
  @DisplayName("Get all entries")
  void getAllEntries() {
    this.palletService.addPalletInfo(
            "123",
            new PalletInfo(
                    "product1",
                    1.0,
                    1.0,
                    10,
                    ProductType.D_PAK
            )
    );
    this.palletService.addPalletInfo(
            "321",
            new PalletInfo(
                    "product2",
                    1.0,
                    1.0,
                    10,
                    ProductType.D_PAK
            )
    );

    assertEquals(2, this.palletService.getAll().size());
  }

  @Test
  @DisplayName("Find product info from gtin")
  void findByGtin() {
    var GTIN = "123";
    this.palletService.addPalletInfo(
            GTIN,
            new PalletInfo(
                    "product1",
                    1.0,
                    2.0,
                    10,
                    ProductType.D_PAK
            )
    );

    var result = this.palletService.findByGtin(GTIN);

    if (result.isEmpty()) {
      fail("No product info with gtin: " + GTIN + " was found");
    } else {
      var info = result.get();
      assertEquals("product1", info.getProductName());
      assertEquals(1.0, info.getProductWeight());
      assertEquals(2.0, info.getProductVolume());
      assertEquals(10, info.getQuantity());
      assertEquals(ProductType.D_PAK, info.getType());
    }
  }

  @Test
  @DisplayName("Find product info from gtin that does not exist")
  void findByInvalidGtin() {
    var result = this.palletService.findByGtin("123");

    if (result.isEmpty()) {
      assertTrue(true);
    } else {
      fail("Result is not empty, but should be");
    }
  }

}

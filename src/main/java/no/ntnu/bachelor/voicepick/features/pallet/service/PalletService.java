package no.ntnu.bachelor.voicepick.features.pallet.service;

import jakarta.persistence.EntityExistsException;
import no.ntnu.bachelor.voicepick.features.pallet.models.PalletInfo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PalletService {

  private Map<String, PalletInfo> products = new HashMap<>();

  /**
   * Returns info about all the stored products
   *
   * @return info about all products
   */
  public List<PalletInfo> getAll() {
    return new ArrayList<>(this.products.values());
  }

  /**
   * Finds info of a product based on the gtin
   * @param gtin of the product to find info of
   *
   * @return optional with the info of the product if it was found. If no product with the gtin provided was found,
   * the optional is empty
   */
  public Optional<PalletInfo> findByGtin(String gtin) {
    var productFound = products.get(gtin);

    Optional<PalletInfo> product;
    if (productFound == null) {
      product = Optional.empty();
    } else {
      product = Optional.of(productFound);
    }
    return product;
  }

  /**
   * Adds info about a product to the stored list
   *
   * @param gtin of the product to add
   * @param palletInfo to be added
   */
  public void addPalletInfo(String gtin, PalletInfo palletInfo) {
    if (this.products.containsKey(gtin))
      throw new EntityExistsException("Product with gtin (" + gtin + ") already exists");

    this.products.put(gtin, palletInfo);
  }

  /**
   * Clears the list of stored product infos
   */
  public void emptyList() {
    this.products.clear();
  }

}

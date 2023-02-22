package no.ntnu.bachelor.voicepick.features.pluck.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.exceptions.EmptyListException;
import no.ntnu.bachelor.voicepick.features.pluck.models.Pluck;
import no.ntnu.bachelor.voicepick.features.pluck.models.PluckList;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.PluckListRepository;
import no.ntnu.bachelor.voicepick.models.Product;
import no.ntnu.bachelor.voicepick.services.ProductService;

/**
 * A service class that exposes method for the pluck list entity
 */
@Service
@RequiredArgsConstructor
public class PluckListService {

  private final ProductService productService;
  private final PluckService pluckService;
  private final PluckListRepository pluckListRepository;

  /**
   * Generates random pluck list
   * 
   * @throws EmptyListException if there are no available products stored in the
   *                            repository
   */
  public PluckList generateRandomPluckList() throws EmptyListException {
    var random = new Random();

    // Generate a random pluck list
    String[] ROUTES = { "1234", "3453", "6859", "3423", "0985", "1352" };
    String[] DESTINATIONS = { "Bunnpris Torghallen", "Kiwi Sundgata", "Kiwi Nedre Strandgate", "Rema 1000 Strandgata",
        "Afrin Dagligvare Ålesund AS", "Olivers & CO Ålesund" };

    var randomDestinationIndex = random.nextInt(DESTINATIONS.length);
    var pluckList = new PluckList(
        ROUTES[randomDestinationIndex],
        DESTINATIONS[randomDestinationIndex]);

    this.pluckListRepository.save(pluckList);

    // Retrive all available products
    var availableProducts = this.productService.getAvailableProducts();

    if (availableProducts.size() == 0) {
      throw new EmptyListException("No available products");
    }

    var MAX_PLUCK_AMOUNT = 10;
    var productsToPluck = this.extractRandomProduct(availableProducts, MAX_PLUCK_AMOUNT);

    // Generate random plucks based on products to pluck
    var PLUCK_AMOUNT_UPPER_BOUND = 10;
    for (var product : productsToPluck) {
      var pluck = new Pluck(
          product,
          random.nextInt((PLUCK_AMOUNT_UPPER_BOUND - 1)) + 1,
          LocalDateTime.now());

      pluckList.addPluck(pluck);
      this.pluckService.savePluck(pluck);
    }

    return pluckList;

  }

  /**
   * Extracts n random number of products from a list of products
   * 
   * @param products a list of products that should be extracted from
   * @param max      the maximum numbers of product to be extracted. Not
   *                 that since it extracts a random number of products this can
   *                 be
   *                 less then the maximum value.
   * @return a set of products
   */
  private Set<Product> extractRandomProduct(List<Product> products, int max) {
    var random = new Random();

    // Copy the list given so we do not alter the original one
    var _products = new ArrayList<Product>(products);

    int numberOfPlucks;
    if (_products.size() < max) {
      numberOfPlucks = random.nextInt(_products.size()) + 1;
    } else {
      numberOfPlucks = random.nextInt(max) + 1;
    }

    var extractedProducts = new HashSet<Product>();
    for (var i = 0; i < numberOfPlucks; i++) {
      var randomProductIndex = random.nextInt(_products.size());
      extractedProducts.add(_products.get(randomProductIndex));
      _products.remove(randomProductIndex);
    }

    return extractedProducts;
  }

}

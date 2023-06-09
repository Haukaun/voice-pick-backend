package no.ntnu.bachelor.voicepick.features.pluck.services;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;

import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.features.authentication.utils.JwtUtil;
import no.ntnu.bachelor.voicepick.features.pluck.models.CargoCarrier;
import no.ntnu.bachelor.voicepick.features.pluck.repositories.CargoCarrierRepository;
import no.ntnu.bachelor.voicepick.services.LocationService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;


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
  private final UserService userService;
  private final PluckListRepository pluckListRepository;
  private final CargoCarrierRepository cargoCarrierRepository;
  private final JwtUtil jwt;
  private final LocationService locationService;

  private final Random random = new Random();

  private static final String[] ROUTES = { "1234", "3453", "6859", "3423", "0985", "1352" };
  private static final String[] DESTINATIONS = { "Bunnpris Torghallen", "Kiwi Sundgata", "Kiwi Nedre Strandgate", "Rema 1000 Strandgata",
          "Afrin Dagligvare Ålesund AS", "Olivers & CO Ålesund" };

  /**
   * Returns a pluck list based on id
   *
   * @param id of the pluck list to find
   * @return optional containing the pluck list if it was found. If not,
   * an empty optional is returned
   */
  public Optional<PluckList> findById(Long id) {
    return this.pluckListRepository.findById(id);
  }

  /**
   * Generates random pluck list
   * 
   * @throws EmptyListException if there are no available products stored in the
   *                            repository
   * @throws JsonProcessingException
   */
  public PluckList generateRandomPluckList(String token) throws EmptyListException, JsonProcessingException {
    
    String userId = jwt.getUid(token);
      
    var optionalUser = this.userService.getUserById(userId);

    if (optionalUser.isEmpty()) {
      throw new EntityNotFoundException("User not found; " + userId);
    }


    // Make sure there are location available
    var locations = this.locationService.getAllPluckListLocations();
    if (locations.isEmpty()) {
      throw new EmptyListException("No available locations");
    }

    // Generate a random pluck list
    var randomDestinationIndex = random.nextInt(DESTINATIONS.length);
    var pluckList = new PluckList(
        ROUTES[randomDestinationIndex],
        DESTINATIONS[randomDestinationIndex],
        optionalUser.get()
        );

    // Add pluck list to random location
    var randomLocation = locations.get(random.nextInt(locations.size()));
    randomLocation.addEntity(pluckList);

    this.pluckListRepository.save(pluckList);

    // Retrieve all available products
    var availableProducts = this.productService.getAvailableProducts();
    if (availableProducts.isEmpty()) {
      throw new EmptyListException("No available products");
    }

    final int MAX_PLUCK_AMOUNT = 10;
    var productsToPluck = this.extractRandomProduct(availableProducts, MAX_PLUCK_AMOUNT);

    // Generate random plucks based on products to pluck
    final int PLUCK_AMOUNT_UPPER_BOUND = 10;
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

    // Copy the list given so we do not alter the original one
    var productsCopy = new ArrayList<Product>(products);

    int numberOfPlucks;
    if (productsCopy.size() < max) {
      numberOfPlucks = random.nextInt(productsCopy.size()) + 1;
    } else {
      numberOfPlucks = random.nextInt(max) + 1;
    }

    var extractedProducts = new HashSet<Product>();
    for (var i = 0; i < numberOfPlucks; i++) {
      var randomProductIndex = random.nextInt(productsCopy.size());
      extractedProducts.add(productsCopy.get(randomProductIndex));
      productsCopy.remove(randomProductIndex);
    }

    return extractedProducts;
  }

  /**
   * Updates the cargo carrier for a given pluck list
   *
   * @param id of the pluck list to update the cargo carrier for
   * @param cargoIdentifier of the cargo carrier to add to the pluck list
   */
  public void updateCargoCarrier(Long id, Long cargoIdentifier) {
    Optional<PluckList> pluckListOpt = this.findById(id);
    Optional<CargoCarrier> cargoCarrierOpt = this.cargoCarrierRepository.findByIdentifier(cargoIdentifier);

    if (pluckListOpt.isEmpty()) {
      throw new EntityNotFoundException("Could not find a pluck list with id: " + id);
    }
    if (cargoCarrierOpt.isEmpty()) {
      throw new EntityNotFoundException("Could not find a cargo carrier with identifier: " + cargoIdentifier);
    }

    var pluckList = pluckListOpt.get();
    var cargoCarrier = cargoCarrierOpt.get();

    cargoCarrier.addPluckList(pluckList);

    this.pluckListRepository.save(pluckList);
  }

}

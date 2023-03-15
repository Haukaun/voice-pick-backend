package no.ntnu.bachelor.voicepick.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.models.Location;
import no.ntnu.bachelor.voicepick.models.Product;
import no.ntnu.bachelor.voicepick.repositories.ProductRepository;

/**
 * A service class for the product model
 */
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository repository;
  private final LocationService locationService;

  /**
   * Adds a product to the repository
   * 
   * @param product to add
   */
  public void addProduct(AddProductRequest product) {
    Location location;

    var result = this.locationService.getLocation(product.getLocation());
    location = result.orElse(null);

    Product productToSave = new Product(
        product.getName(),
        location,
        product.getWeight(),
        product.getVolume(),
        product.getQuantity(),
        product.getType(),
        product.getStatus());

    this.repository.save(productToSave);
  }

  /**
   * Returns a list of products that are available. By available
   * we mean a product that has a location so it can be plucked.
   * 
   * @return a list of products
   */
  public List<Product> getAvailableProducts() {
    return this.repository.findByLocationIsNotNull();
  }

  /**
   * Returns a list of all products stored in the repository.
   * 
   * @return a list of all products
   */
  public List<Product> getAllProducts() {
    return this.repository.findAll();
  }

  /**
   * Returns all of products with the same name as the one given
   * 
   * @param name name of the product to filter by
   * @return a list of products with the name given
   */
  public List<Product> getProductsByName(String name) {
    return this.repository.findByName(name);
  }

}

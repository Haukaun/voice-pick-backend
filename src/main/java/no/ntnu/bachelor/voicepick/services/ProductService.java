package no.ntnu.bachelor.voicepick.services;

import java.util.List;

import no.ntnu.bachelor.voicepick.models.LocationType;
import no.ntnu.bachelor.voicepick.models.Status;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
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
  private final WarehouseService warehouseService;

  /**
   * Adds a product to the repository
   * 
   * @param product to add
   */
  public void addProduct(AddProductRequest request, Warehouse warehouse) {
    var optionalLocation = this.locationService.getLocationByCodeAndWarehouseAndLocationType(request.getLocation(), warehouse, LocationType.PRODUCT);

    Product productToSave;
    if (optionalLocation.isPresent()) {
      productToSave = new Product(
          request.getName(),
          request.getWeight(),
          request.getVolume(),
          request.getQuantity(),
          request.getType(),
          Status.READY);
      var location = optionalLocation.get();
      location.addEntity(productToSave);
    } else {
      productToSave = new Product(
          request.getName(),
          request.getWeight(),
          request.getVolume(),
          request.getQuantity(),
          request.getType(),
          Status.WITHOUT_LOCATION);
    }
    warehouse.addProduct(productToSave);
    this.repository.save(productToSave);
  }

  /**
   * Returns a list of products that are available. By available
   * we mean a product that has a location so it can be plucked.
   * 
   * @return a list of products
   */
  public List<Product> getAvailableProducts() {
    return this.repository.findProductsWithLocation(Status.INACTIVE);
  }

  /**
   * Returns a list of all products that does not have a location
   *
   * @param name of the products to search for
   * @return a list of all products without a location
   */
  public List<Product> getProductsWithoutLocation(String name) {
    return this.repository.findProductsWithoutLocation(name);
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
   * Returns all products with the same name as the one given
   * 
   * @param name name of the product to filter by
   * @return a list of products with the name given
   */
  public List<Product> getProductsByName(String name) {
    return this.repository.findByName(name);
  }

  /**
   * Returns a list of all available products filtered by name
   *
   * @param name of the products to search for
   * @return a list of products
   */
  public List<Product> getAvailableProductsByName(String name) {
    return this.repository.findProductsWithLocationByName(name, Status.INACTIVE);
  }

  /**
   * Deletes all products with the given name
   *
   * @param name of the product to be deleted
   */
  public void deleteAll(String name) {
    var productsFound = this.getProductsByName(name);

    for (Product product : productsFound) {
      product.setStatus(Status.INACTIVE);
      this.repository.save(product);
    }
  }

}

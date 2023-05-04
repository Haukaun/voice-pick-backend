package no.ntnu.bachelor.voicepick.services;

import java.util.List;
import java.util.Optional;

import no.ntnu.bachelor.voicepick.models.*;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.dtos.UpdateProductRequest;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.repositories.ProductRepository;

/**
 * A service class for the product model
 */
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final LocationService locationService;
  private final WarehouseService warehouseService;
  private final UserService userService;

  /**
   * Adds a product to the repository
   * 
   * @param request with information about product to add
   * @param warehouse to add the product to
   */
  public void addProduct(AddProductRequest request, Warehouse warehouse) {
    var optionalLocation = this.locationService.getLocationByCodeAndWarehouseAndLocationType(request.getLocationCode(), warehouse, LocationType.PRODUCT);

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
    this.productRepository.save(productToSave);
  }

  /**
   * Returns a list of products that are available in a warehouse. By available
   * its meant a product that has a location so it can be plucked.
   * 
   * @return a list of products
   */
  public List<Product> getAvailableProducts(Warehouse warehouse) {
    return this.productRepository.findProductsInWarehouseWithLocationAndQuantity(warehouse.getId(), Status.INACTIVE);
  }

  /**
   * Returns a list of all products that does not have a location
   *
   * @param name of the products to search for
   * @return a list of all products without a location
   */
  public List<Product> getProductsWithoutLocation(String name) {
    return this.productRepository.findProductsWithoutLocation(name);
  }

  /**
   * Returns a list of all products stored in the repository.
   * 
   * @return a list of all products
   */
  public List<Product> getAllProducts() {
    return this.productRepository.findAll();
  }

  /**
   * Returns all products with the same name as the one given
   * 
   * @param name name of the product to filter by
   * @return a list of products with the name given
   */
  public List<Product> getProductsByName(String name) {
    return this.productRepository.findByName(name);
  }

  /**
   * Returns a list of all available products filtered by name
   *
   * @param name of the products to search for
   * @return a list of products
   */
  public List<Product> getAvailableProductsByName(String name) {
    return this.productRepository.findProductsWithLocationByName(name, Status.INACTIVE);
  }

  public List<Product> getAllAvailableProductsByWarehouse(Warehouse warehouse) {
    return this.productRepository.findByWarehouse(warehouse);
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
      this.productRepository.save(product);
    }
  }

  /**
   * Deletes a specific product
   *
   * @param productId of the product to delete
   */
  public void deleteSpecificProduct(Long productId, Warehouse wh) {
    if (wh == null){
      throw new IllegalArgumentException("You must specify a warehouse!");
    }
    Optional<Product> optionalProduct = productRepository.findByIdAndWarehouse(productId, wh);
    if (optionalProduct.isEmpty()) {
      throw new EntityNotFoundException("Product with id: " + productId + " was not found.");
    }

    var product = optionalProduct.get();
    product.setStatus(Status.INACTIVE);

    this.productRepository.save(product);
  }

  public Product updateProduct(Long productId, UpdateProductRequest dto) {
    var optionalProduct = this.productRepository.findById(productId);
    if (optionalProduct.isEmpty()) {
      throw new EntityNotFoundException("Could not find product id: " + productId);
    }
    var product = optionalProduct.get();

    var user = this.userService.getCurrentUser();
    var optionalWarehouse = this.warehouseService.findWarehouseByUser(user);
    if (optionalWarehouse.isEmpty()) {
      throw new EntityNotFoundException("User is not in a warehouse.");
    }

    var optionalLocation = this.locationService.getLocationByCodeAndWarehouse(dto.getLocationCode(), optionalWarehouse.get());

    Status status;
    if (dto.getQuantity() == 0) {
      status = Status.EMPTY;
    } else if (optionalLocation.isEmpty()) {
      status = Status.WITHOUT_LOCATION;
    } else {
      status = Status.READY;
    }

    product.setName(dto.getName());
    product.setWeight(dto.getWeight());
    product.setVolume(dto.getVolume());
    product.setQuantity(dto.getQuantity());
    product.setType(dto.getType());
    product.setLocation(optionalLocation.orElse(null));
    product.setStatus(status);

    this.productRepository.save(product);

    return product;
  }
}

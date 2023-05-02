package no.ntnu.bachelor.voicepick.repositories;

import java.util.List;
import java.util.Optional;

import no.ntnu.bachelor.voicepick.models.Status;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.models.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Returns all products that has a location and a minimum quantity of 1 and filtered out the status provided.
   *
   * @param status the status to not be included in the result
   * @return a list of all products with a location and filtered status
   */
  @Query("SELECT p FROM Product p WHERE p.location IS NOT NULL AND p.status != :status AND p.quantity > 0")
  List<Product> findProductsWithLocationAndQuantity(@Param("status") Status status);

  @Query("SELECT p FROM Product p WHERE p.location IS NOT NULL AND p.status != :status AND p.name LIKE :name")
  List<Product> findProductsWithLocationByName(@Param("name") String name, @Param("status") Status status);

  /**
   * Returns all products that does not have a location
   *
   * @return all products without a location
   */
  @Query("SELECT p FROM Product p WHERE p.location IS NULL AND p.name LIKE :name")
  List<Product> findProductsWithoutLocation(@Param("name") String name);

  Optional<Product> findByIdAndWarehouse(Long id, Warehouse warehouse);

  /**
   * Returns all products that are not inactive
   *
   * @return all inactive products
   */
  @Query("SELECT p FROM Product p WHERE p.warehouse = :warehouse AND p.status != 3")
  List<Product> findByWarehouse(@Param("warehouse") Warehouse warehouse);


  List<Product> findByName(String name);

}

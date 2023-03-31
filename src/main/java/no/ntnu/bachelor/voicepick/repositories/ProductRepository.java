package no.ntnu.bachelor.voicepick.repositories;

import java.util.List;

import no.ntnu.bachelor.voicepick.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.models.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Returns all products that has a location and filtered out the status provided.
   *
   * @param status the status to not be included in the result
   * @return a list of all products with a location and filtered status
   */
  @Query("SELECT p FROM Product p WHERE p.location IS NOT NULL AND p.status != :status")
  List<Product> findProductsWithLocation(@Param("status") Status status);

  @Query("SELECT p FROM Product p WHERE p.location IS NOT NULL AND p.status != :status AND p.name LIKE :name")
  List<Product> findProductsWithLocationByName(@Param("name") String name, @Param("status") Status status);

  /**
   * Returns all products that does not have a location
   *
   * @return all products without a location
   */
  @Query("SELECT p FROM Product p WHERE p.location IS NULL AND p.name LIKE :name")
  List<Product> findProductsWithoutLocation(@Param("name") String name);

  List<Product> findByName(String name);

}

package no.ntnu.bachelor.voicepick.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import no.ntnu.bachelor.voicepick.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByLocationIsNotNull();

}

package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

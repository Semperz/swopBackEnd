package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}

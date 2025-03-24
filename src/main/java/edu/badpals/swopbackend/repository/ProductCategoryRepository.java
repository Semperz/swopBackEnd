package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.model.ProductCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Transactional
    void deleteByProduct(Product product);

    List<ProductCategory> findByProduct(Product product);
}

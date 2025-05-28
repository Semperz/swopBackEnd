package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductCategories_Category_Id(Long categoryId);
}

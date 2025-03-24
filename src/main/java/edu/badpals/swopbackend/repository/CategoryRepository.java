package edu.badpals.swopbackend.repository;

import edu.badpals.swopbackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

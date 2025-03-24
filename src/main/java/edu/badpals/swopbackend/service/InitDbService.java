package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.ProductDto;
import edu.badpals.swopbackend.model.Category;
import edu.badpals.swopbackend.model.Customer;
import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.model.ProductCategory;
import edu.badpals.swopbackend.repository.CategoryRepository;
import edu.badpals.swopbackend.repository.CustomerRepository;
import edu.badpals.swopbackend.repository.ProductCategoryRepository;
import edu.badpals.swopbackend.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InitDbService {

    private final CustomerRepository customerRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public InitDbService(CustomerRepository customerRepository, ProductCategoryRepository productCategoryRepository, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void initDatabase() {
        if (customerRepository.count() == 0) { // Si no hay clientes en la base de datos
            Customer customer1 = new Customer(
                    "john.doe@example.com", passwordEncoder.encode("password123"),
                    "John Doe", "123 Main St", "123 Main St", "USA", "1234567890"
            );

            Customer customer2 = new Customer(
                    "jane.doe@example.com", passwordEncoder.encode("password456"),
                    "Jane Doe", "456 Elm St", "456 Elm St", "Canada", "0987654321"
            );

            customerRepository.save(customer1);
            customerRepository.save(customer2);

            // Crear categorías de Star Wars
            Category category1 = new Category("Star Wars Action Figures", "Figuras de acción de Star Wars", "thumbnail1.jpg");
            Category category2 = new Category("Star Wars LEGO Sets", "Sets de LEGO de Star Wars", "thumbnail2.jpg");
            Category category3 = new Category("Star Wars Collectibles", "Coleccionables de Star Wars", "thumbnail3.jpg");
            categoryRepository.save(category1);
            categoryRepository.save(category2);
            categoryRepository.save(category3);

            // Crear productos de Star Wars
            Product product1 = new Product("SW001", "Luke Skywalker Action Figure", new BigDecimal("19.99"), 0.5, "Figura de acción de Luke Skywalker", "thumbnail1.jpg", "image1.jpg", null, 100);
            Product product2 = new Product("SW002", "Darth Vader Action Figure", new BigDecimal("24.99"), 0.6, "Figura de acción de Darth Vader", "thumbnail2.jpg", "image2.jpg", null, 100);
            Product product3 = new Product("SW003", "Millennium Falcon LEGO Set", new BigDecimal("149.99"), 2.5, "Set de LEGO del Millennium Falcon", "thumbnail3.jpg", "image3.jpg", null, 50);
            productRepository.save(product1);
            productRepository.save(product2);
            productRepository.save(product3);

            // Crear relaciones entre productos y categorías
            ProductCategory productCategory1 = new ProductCategory(product1, category1);
            ProductCategory productCategory2 = new ProductCategory(product1, category3);
            ProductCategory productCategory3 = new ProductCategory(product2, category1);
            ProductCategory productCategory4 = new ProductCategory(product2, category3);
            ProductCategory productCategory5 = new ProductCategory(product3, category2);
            productCategoryRepository.save(productCategory1);
            productCategoryRepository.save(productCategory2);
            productCategoryRepository.save(productCategory3);
            productCategoryRepository.save(productCategory4);
            productCategoryRepository.save(productCategory5);

            product1.setCategories(List.of(productCategory1, productCategory2));
            product2.setCategories(List.of(productCategory3, productCategory4));
            product3.setCategories(List.of(productCategory5));
            productRepository.save(product1);
            productRepository.save(product2);
            productRepository.save(product3);
            System.out.println("✅ Base de datos inicializada con clientes de prueba.");
        } else {
            System.out.println("ℹ️ La base de datos ya contiene datos, no se inicializará.");
        }
    }
}


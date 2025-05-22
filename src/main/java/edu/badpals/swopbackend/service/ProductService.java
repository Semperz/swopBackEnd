package edu.badpals.swopbackend.service;
import edu.badpals.swopbackend.dto.ProductDto;
import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.model.Category;
import edu.badpals.swopbackend.model.ProductCategory;
import edu.badpals.swopbackend.repository.ProductRepository;
import edu.badpals.swopbackend.repository.CategoryRepository;
import edu.badpals.swopbackend.repository.ProductCategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductCategoryRepository productCategoryRepository,
                          ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.modelMapper = modelMapper;
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = modelMapper.map(product, ProductDto.class);
        // Extraer IDs de categorías
        List<Long> categoryIds = product.getCategories().stream()
                .map(pc -> pc.getCategory().getId())
                .collect(Collectors.toList());
        dto.setCategories(categoryIds);
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        return modelMapper.map(dto, Product.class);
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = toEntity(productDto);
        Product savedProduct = productRepository.save(product);

        // Asociar categorías
        List<ProductCategory> productCategories = productDto.getCategories().stream()
                .map(categoryId -> {
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                    return new ProductCategory(savedProduct, category);
                })
                .collect(Collectors.toList());

        productCategoryRepository.saveAll(productCategories);
        savedProduct.setCategories(productCategories);

        return toDto(savedProduct);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return toDto(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Actualiza campos básicos con modelMapper
        modelMapper.map(productDto, existingProduct);

        // Actualiza relaciones con categorías
        List<ProductCategory> currentCategories = productCategoryRepository.findByProduct(existingProduct);
        Set<Long> currentCategoryIds = currentCategories.stream()
                .map(pc -> pc.getCategory().getId())
                .collect(Collectors.toSet());

        Set<Long> newCategoryIds = new HashSet<>(productDto.getCategories());

        List<ProductCategory> categoriesToRemove = currentCategories.stream()
                .filter(pc -> !newCategoryIds.contains(pc.getCategory().getId()))
                .collect(Collectors.toList());

        List<ProductCategory> categoriesToAdd = newCategoryIds.stream()
                .filter(categoryId -> !currentCategoryIds.contains(categoryId))
                .map(categoryId -> {
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                    return new ProductCategory(existingProduct, category);
                })
                .collect(Collectors.toList());

        productCategoryRepository.deleteAll(categoriesToRemove);
        productCategoryRepository.saveAll(categoriesToAdd);

        existingProduct.setCategories(productCategoryRepository.findByProduct(existingProduct));
        Product updatedProduct = productRepository.save(existingProduct);

        return toDto(updatedProduct);
    }
}

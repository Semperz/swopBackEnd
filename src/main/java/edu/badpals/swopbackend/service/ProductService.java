package edu.badpals.swopbackend.service;
import edu.badpals.swopbackend.dto.ProductDto;
import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.model.Category;
import edu.badpals.swopbackend.model.ProductCategory;
import edu.badpals.swopbackend.repository.ProductRepository;
import edu.badpals.swopbackend.repository.CategoryRepository;
import edu.badpals.swopbackend.repository.ProductCategoryRepository;
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

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    private ProductDto toDto(Product product) {
        List<Long> categoryIds = product.getCategories().stream()
                .map(pc -> pc.getCategory().getId())
                .collect(Collectors.toList());

        return new ProductDto(
                product.getId(), product.getSku(), product.getName(), product.getPrice(),
                product.getWeight(), product.getDescriptions(), product.getThumbnail(),
                product.getImage(), categoryIds, product.getStock(), product.getCreateDate()
        );
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setSku(dto.getSku());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setWeight(dto.getWeight());
        product.setDescriptions(dto.getDescriptions());
        product.setThumbnail(dto.getThumbnail());
        product.setImage(dto.getImage());
        product.setStock(dto.getStock());
        product.setCreateDate(dto.getCreateDate());
        return product;
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = toEntity(productDto);
        Product savedProduct = productRepository.save(product);

        // Guardamos la relación en ProductCategory
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
        return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
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
        // Buscar el producto en la base de datos
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Actualizar datos del producto
        existingProduct.setSku(productDto.getSku());
        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setWeight(productDto.getWeight());
        existingProduct.setDescriptions(productDto.getDescriptions());
        existingProduct.setThumbnail(productDto.getThumbnail());
        existingProduct.setImage(productDto.getImage());
        existingProduct.setStock(productDto.getStock());

        // Categorias actuales del producto
        List<ProductCategory> currentCategories = productCategoryRepository.findByProduct(existingProduct);

        // Extraer los IDs de las categorías actuales
        Set<Long> currentCategoryIds = currentCategories.stream()
                .map(pc -> pc.getCategory().getId())
                .collect(Collectors.toSet());

        // Extraer los nuevos IDs de categorías del DTO
        Set<Long> newCategoryIds = new HashSet<>(productDto.getCategories());

        // Determinar categorías a eliminar (las que ya no están en la nueva lista)
        List<ProductCategory> categoriesToRemove = currentCategories.stream()
                .filter(pc -> !newCategoryIds.contains(pc.getCategory().getId()))
                .collect(Collectors.toList());

        // Determinar categorías a añadir (las nuevas que no estaban antes)
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

        // Actualizar la lista de categorías en el producto
        existingProduct.setCategories(productCategoryRepository.findByProduct(existingProduct));

        Product updatedProduct = productRepository.save(existingProduct);

        return toDto(updatedProduct);
    }

}

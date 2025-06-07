package edu.badpals.swopbackend.service;
import edu.badpals.swopbackend.dto.ProductDto;
import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.model.Category;
import edu.badpals.swopbackend.model.ProductCategory;
import edu.badpals.swopbackend.repository.ProductRepository;
import edu.badpals.swopbackend.repository.CategoryRepository;
import edu.badpals.swopbackend.repository.ProductCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    public List<ProductDto> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByProductCategories_Category_Id(categoryId);
        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));

        try {
            // Actualiza los campos básicos manualmente (excepto categorías)
            existingProduct.setName(productDto.getName());
            existingProduct.setDescriptions(productDto.getDescriptions());
            existingProduct.setPrice(productDto.getPrice());
            existingProduct.setImage(productDto.getImage());
            existingProduct.setCreateDate(productDto.getCreateDate());
            existingProduct.setSku(productDto.getSku());
            existingProduct.setWeight(productDto.getWeight());
            existingProduct.setThumbnail(productDto.getThumbnail());
            existingProduct.setStock(productDto.getStock());
            existingProduct.setAuctionEndTime(productDto.getAuctionEndTime());

            // Gestiona las categorías
            // Elimina todas las relaciones antiguas
            List<ProductCategory> oldCategories = new ArrayList<>(existingProduct.getCategories());
            for (ProductCategory pc : oldCategories) {
                pc.setProduct(null); // Rompe la relación
            }
            existingProduct.getCategories().clear();

            // Añade las nuevas relaciones
            List<ProductCategory> newProductCategories = new ArrayList<>();
            for (Long categoryId : productDto.getCategories()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
                ProductCategory newPc = new ProductCategory(existingProduct, category);
                newProductCategories.add(newPc);
            }
            existingProduct.getCategories().addAll(newProductCategories);

            // Guarda el producto actualizado
            Product updatedProduct = productRepository.save(existingProduct);
            return toDto(updatedProduct);
        } catch (Exception ex) {
            throw new RuntimeException("Error updating product: " + ex.getMessage(), ex);
        }
    }

    public ProductDto getAuctedProduct(){
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new RuntimeException("No products available for auction.");
        }
        // Filtrar productos que tienen una fecha de finalización de subasta
        List<Product> auctedProducts = products.stream()
                .filter(product -> product.getAuctionEndTime() != null)
                .toList();

        if (auctedProducts.isEmpty()) {
            throw new RuntimeException("No products available for auction.");
        }

        // Retornar el primer producto con subasta
        return toDto(auctedProducts.get(0));
    }
}

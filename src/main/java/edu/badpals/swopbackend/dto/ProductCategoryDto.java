package edu.badpals.swopbackend.dto;

import edu.badpals.swopbackend.model.Category;
import edu.badpals.swopbackend.model.Product;


public class ProductCategoryDto {
    private Long id;
    private Product product;
    private Category category;

    public ProductCategoryDto() {
    }

    public ProductCategoryDto(Long id, Product product, Category category) {
        this.id = id;
        this.product = product;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
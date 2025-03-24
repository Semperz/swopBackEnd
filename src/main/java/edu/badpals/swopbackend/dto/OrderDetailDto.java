package edu.badpals.swopbackend.dto;

import edu.badpals.swopbackend.model.Order;
import edu.badpals.swopbackend.model.Product;


import java.math.BigDecimal;


public class OrderDetailDto {
    private Long id;
    private Order order;
    private Product product;
    private BigDecimal price;
    private String sku;
    private Integer quantity;

    public OrderDetailDto() {
    }

    public OrderDetailDto(Long id, Order order, Product product, BigDecimal price, String sku, Integer quantity) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.price = price;
        this.sku = sku;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
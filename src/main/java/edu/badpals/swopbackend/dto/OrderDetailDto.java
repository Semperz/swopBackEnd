package edu.badpals.swopbackend.dto;

import edu.badpals.swopbackend.model.Order;
import edu.badpals.swopbackend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private Long id;
    private Order order;
    private Product product;
    private BigDecimal price;
    private String sku;
    private Integer quantity;
}
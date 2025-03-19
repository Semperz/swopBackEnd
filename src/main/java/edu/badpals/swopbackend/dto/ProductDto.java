package edu.badpals.swopbackend.dto;

import edu.badpals.swopbackend.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private Double weight;
    private String descriptions;
    private String thumbnail;
    private String image;
    private Category category;
    private Integer stock;
    private LocalDateTime createDate;
}
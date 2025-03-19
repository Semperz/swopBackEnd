package edu.badpals.swopbackend.dto;

import edu.badpals.swopbackend.model.Category;
import edu.badpals.swopbackend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDto {
    private Long id;
    private Product product;
    private Category category;
}
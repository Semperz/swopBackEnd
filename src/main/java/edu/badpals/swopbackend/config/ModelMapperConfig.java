package edu.badpals.swopbackend.config;

import edu.badpals.swopbackend.dto.OrderDetailDto;
import edu.badpals.swopbackend.dto.ProductDto;
import edu.badpals.swopbackend.model.OrderDetail;
import edu.badpals.swopbackend.model.Product;
import edu.badpals.swopbackend.model.ProductCategory;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Product, ProductDto>() {
            @Override
            protected void configure() {
                using(ctx -> ((List<ProductCategory>) ctx.getSource()).stream()
                        .map(ProductCategory::getId)
                        .collect(Collectors.toList()))
                        .map(source.getCategories(), destination.getCategories());
            }
        });
        modelMapper.addMappings(new PropertyMap<OrderDetail, OrderDetailDto>() {
            @Override
            protected void configure() {
                map().setOrder(source.getOrder().getId());  // Solo el ID
                map().setProduct(source.getProduct().getId()); // Lo mismo si product es entidad
            }
        });

        return modelMapper;
    }
}

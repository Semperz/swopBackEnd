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
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Product, ProductDto>() {
            @Override
            protected void configure() {
                skip(destination.getCategories()); // Evita errores al mapear a DTO
            }
        });

        modelMapper.addMappings(new PropertyMap<ProductDto, Product>() {
            @Override
            protected void configure() {
                skip(destination.getCategories()); // Evita sobrescribir la lista con nulls
            }
        });
        modelMapper.addMappings(new PropertyMap<OrderDetail, OrderDetailDto>() {
            @Override
            protected void configure() {
                map().setOrder(source.getOrder().getId());  // Solo el ID
                map().setProduct(source.getProduct().getId()); // Lo mismo si product es entidad
            }
        });
        modelMapper.typeMap(ProductDto.class, Product.class).addMappings(mapper -> {
            mapper.skip(Product::setId);
        });

        return modelMapper;
    }
}

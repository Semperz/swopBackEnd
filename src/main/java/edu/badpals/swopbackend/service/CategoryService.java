package edu.badpals.swopbackend.service;

import edu.badpals.swopbackend.dto.CategoryDto;
import edu.badpals.swopbackend.model.Category;
import edu.badpals.swopbackend.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    private CategoryDto toDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    private Category toEntity(CategoryDto dto) {
        return modelMapper.map(dto, Category.class);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return toDto(savedCategory);
    }

    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::toDto).collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
        return toDto(category);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        existingCategory.setName(categoryDto.getName());
        existingCategory.setDescription(categoryDto.getDescription());
        existingCategory.setThumbnail(categoryDto.getThumbnail());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return toDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
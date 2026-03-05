package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.CategoryDto;
import com.springboot.ecommerce.dtos.CreateCategoryRequest;
import com.springboot.ecommerce.dtos.UpdateCategoryRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
	CategoryDto createCategory(Long userId, CreateCategoryRequest request);
	List<CategoryDto> getAllCategories();
	CategoryDto getCategoryById(Long categoryId);
	CategoryDto updateCategory(Long userId, Long categoryId, UpdateCategoryRequest request);
	void deleteCategory(Long userId, Long categoryId);
}

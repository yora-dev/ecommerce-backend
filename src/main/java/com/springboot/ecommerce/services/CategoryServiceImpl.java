package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.CategoryDto;
import com.springboot.ecommerce.dtos.CreateCategoryRequest;
import com.springboot.ecommerce.dtos.UpdateCategoryRequest;
import com.springboot.ecommerce.entities.Category;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.exceptions.CategoryNotFoundException;
import com.springboot.ecommerce.exceptions.UnauthorizedAdminException;
import com.springboot.ecommerce.exceptions.UnauthorizedSellerException;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.repositories.CategoryRepository;
import com.springboot.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	private CategoryRepository categoryRepository;
	private UserRepository userRepository;

	@Override
	public CategoryDto createCategory(Long userId, CreateCategoryRequest request) {
		User user =
				userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		if(!user.isAdmin()) {
			throw new UnauthorizedAdminException("Only admins can create category");
		}

		Category category = new Category();
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		category.setCreatedAt(LocalDateTime.now());
		categoryRepository.save(category);

		return new CategoryDto().toDto(category);
	}

	@Override
	public List<CategoryDto> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();

		return categories
				.stream()
				.map(
						(category)-> new CategoryDto().toDto(category)).toList();
	}

	@Override
	public CategoryDto getCategoryById(Long categoryId) {

		Category category =  categoryRepository
				.findById(categoryId)
				.orElseThrow(CategoryNotFoundException::new);

		return new CategoryDto().toDto(category);
	}

	@Override
	public CategoryDto updateCategory(Long userId, Long categoryId, UpdateCategoryRequest request) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);

		if(!user.isAdmin()) {
			throw new UnauthorizedAdminException("Only admins can update category");
		}

		if((request.getName() != null)) {
			category.setName(request.getName());
		}

		if((request.getDescription() != null)) {
			category.setDescription(request.getDescription());
		}

		category.setUpdatedAt(LocalDateTime.now());
		categoryRepository.save(category);

		return new CategoryDto().toDto(category);
	}

	@Override
	public void deleteCategory(Long userId, Long categoryId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);

		if(!user.isAdmin()) {
			throw new UnauthorizedAdminException("Only admins can delete category.");
		}

		categoryRepository.delete(category);
	}
}

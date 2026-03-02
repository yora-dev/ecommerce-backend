package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CategoryDto;
import com.springboot.ecommerce.dtos.CreateCategoryRequest;
import com.springboot.ecommerce.dtos.UpdateCategoryRequest;
import com.springboot.ecommerce.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
	private CategoryService categoryService;

	@PostMapping
	@PreAuthorize("hasRole('SYSADMIN')")
	public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
			@AuthenticationPrincipal Long userId,
			@RequestBody CreateCategoryRequest request) {
		ApiResponse<CategoryDto> response = new ApiResponse<>(
				true,
				null,
				categoryService.createCategory(userId, request));

		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
		ApiResponse<List<CategoryDto>> response = new ApiResponse<>(
				true,
				null,
				categoryService.getAllCategories());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(@PathVariable Long categoryId) {
		ApiResponse<CategoryDto> response = new ApiResponse<>(
				true,
				null,
				categoryService.getCategoryById(categoryId));

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{categoryId}")
	@PreAuthorize("hasRole('SYSADMIN')")
	public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
			@AuthenticationPrincipal Long userId,
			@PathVariable Long categoryId,
			@RequestBody UpdateCategoryRequest request) {
		ApiResponse<CategoryDto> response = new ApiResponse<>(
				true,
				null,
				categoryService.updateCategory(userId, categoryId, request));

		return ResponseEntity.ok(response);
	}


	@DeleteMapping("/{categoryId}")
	@PreAuthorize("hasRole('SYSADMIN')")
	public ResponseEntity<ApiResponse<Void>> deleteCategory(
			@AuthenticationPrincipal Long userId,
			@PathVariable Long categoryId) {
		categoryService.deleteCategory(userId, categoryId);

		return ResponseEntity.noContent().build();
	}
}

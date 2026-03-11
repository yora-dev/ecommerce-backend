package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CategoryDto;
import com.springboot.ecommerce.dtos.CreateCategoryRequest;
import com.springboot.ecommerce.dtos.UpdateCategoryRequest;
import com.springboot.ecommerce.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

	@Mock
	private CategoryService categoryService;
	@InjectMocks
	private CategoryController controller;

	@Test
	void createCategoryReturnsCreatedResponse() {
		// Arrange
		CreateCategoryRequest request = new CreateCategoryRequest();
		request.setName("Electronics");
		request.setDescription("Devices and gadgets");
		CategoryDto dto = categoryDto(10L, "Electronics");

		when(categoryService.createCategory(1L, request)).thenReturn(dto);

		// Act
		ResponseEntity<ApiResponse<CategoryDto>> response = controller.createCategory(1L, request);

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isSuccess());
		assertNull(response.getBody().getErrors());
		assertEquals(dto, response.getBody().getData());
		verify(categoryService).createCategory(1L, request);
	}

	@Test
	void getAllCategoriesReturnsAllCategories() {
		// Arrange
		List<CategoryDto> categories = List.of(categoryDto(1L, "Electronics"), categoryDto(2L, "Books"));
		when(categoryService.getAllCategories()).thenReturn(categories);

		// Act
		ResponseEntity<ApiResponse<List<CategoryDto>>> response = controller.getAllCategories();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(categories, response.getBody().getData());
		verify(categoryService).getAllCategories();
	}

	@Test
	void getCategoryByIdReturnsCategory() {
		// Arrange
		CategoryDto dto = categoryDto(5L, "Fashion");
		when(categoryService.getCategoryById(5L)).thenReturn(dto);

		// Act
		ResponseEntity<ApiResponse<CategoryDto>> response = controller.getCategoryById(5L);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(dto, response.getBody().getData());
		verify(categoryService).getCategoryById(5L);
	}

	@Test
	void updateCategoryReturnsUpdatedCategory() {
		// Arrange
		UpdateCategoryRequest request = new UpdateCategoryRequest();
		request.setName("Updated Electronics");
		request.setDescription("Updated description");
		CategoryDto dto = categoryDto(10L, "Updated Electronics");
		when(categoryService.updateCategory(1L, 10L, request)).thenReturn(dto);

		// Act
		ResponseEntity<ApiResponse<CategoryDto>> response = controller.updateCategory(1L, 10L, request);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(dto, response.getBody().getData());
		verify(categoryService).updateCategory(1L, 10L, request);
	}

	@Test
	void deleteCategoryInvokesServiceAndReturnsNoContent() {
		// Act
		ResponseEntity<ApiResponse<Void>> response = controller.deleteCategory(1L, 10L);

		// Assert
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertNull(response.getBody());
		verify(categoryService).deleteCategory(1L, 10L);
	}

	@Test
	void createCategoryPropagatesServiceFailure() {
		// Arrange
		CreateCategoryRequest request = new CreateCategoryRequest();
		RuntimeException expected = new RuntimeException("duplicate category");
		when(categoryService.createCategory(1L, request)).thenThrow(expected);

		// Act
		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> controller.createCategory(1L, request));

		// Assert
		assertSame(expected, thrown);
		verify(categoryService).createCategory(1L, request);
	}

	private CategoryDto categoryDto(Long id, String name) {
		return CategoryDto.builder()
				.categoryId(id)
				.name(name)
				.description(name + " description")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
	}
}
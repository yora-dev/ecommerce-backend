package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CategoryDto;
import com.springboot.ecommerce.dtos.CreateCategoryRequest;
import com.springboot.ecommerce.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

	@Test
	void createCategory() {
		// Arrange
		CategoryService categoryService = mock(CategoryService.class);
		CategoryController controller = new CategoryController(categoryService);

		CreateCategoryRequest request = new CreateCategoryRequest();
		request.setName("Electronics");
		request.setDescription("Devices and gadgets");

		Long userId = 1L;

		CategoryDto dto = CategoryDto.builder()
			.categoryId(10L)
			.name("Electronics")
			.description("Devices and gadgets")
			.build();

		when(categoryService.createCategory(userId, request)).thenReturn(dto);

		// Act
		ResponseEntity<ApiResponse<CategoryDto>> response = controller.createCategory(userId, request);

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());

		ApiResponse<CategoryDto> body = response.getBody();
		assertTrue(body.isSuccess());
		assertNull(body.getErrors());
		assertEquals(dto, body.getData());

		verify(categoryService, times(1)).createCategory(userId, request);
	}
}
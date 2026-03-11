package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CreateProductRequest;
import com.springboot.ecommerce.dtos.ProductDto;
import com.springboot.ecommerce.dtos.UpdateProductDto;
import com.springboot.ecommerce.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

	@Mock
	private ProductService productService;
	@InjectMocks
	private ProductController controller;

	@Test
	void createProductReturnsCreatedResponse() {
		CreateProductRequest request = createProductRequest();
		ProductDto dto = productDto(11L, 3L, 7L, "Phone");
		when(productService.createProduct(7L, request)).thenReturn(dto);

		ResponseEntity<ApiResponse<ProductDto>> response = controller.createProduct(7L, request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isSuccess());
		assertEquals(dto, response.getBody().getData());
		verify(productService).createProduct(7L, request);
	}

	@Test
	void getByIdReturnsProduct() {
		ProductDto dto = productDto(11L, 3L, 7L, "Phone");
		when(productService.getProductById(11L)).thenReturn(dto);

		ResponseEntity<ApiResponse<ProductDto>> response = controller.getById(11L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(dto, response.getBody().getData());
		verify(productService).getProductById(11L);
	}

	@Test
	void getAllProductsReturnsProducts() {
		List<ProductDto> products = List.of(productDto(11L, 3L, 7L, "Phone"), productDto(12L, 4L, 8L, "Book"));
		when(productService.getAllProducts()).thenReturn(products);

		ResponseEntity<ApiResponse<List<ProductDto>>> response = controller.getAllProducts();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(products, response.getBody().getData());
		verify(productService).getAllProducts();
	}

	@Test
	void getAllCategoryProductsReturnsCategoryProducts() {
		List<ProductDto> products = List.of(productDto(11L, 3L, 7L, "Phone"));
		when(productService.getProductsByCategory(3L)).thenReturn(products);

		ResponseEntity<ApiResponse<List<ProductDto>>> response = controller.getAllCategoryProducts(3L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(products, response.getBody().getData());
		verify(productService).getProductsByCategory(3L);
	}

	@Test
	void getAllSellerProductsReturnsSellerProducts() {
		List<ProductDto> products = List.of(productDto(11L, 3L, 7L, "Phone"));
		when(productService.getProductsBySeller(7L)).thenReturn(products);

		ResponseEntity<ApiResponse<List<ProductDto>>> response = controller.getAllSellerProducts(7L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(products, response.getBody().getData());
		verify(productService).getProductsBySeller(7L);
	}

	@Test
	void updateProductReturnsUpdatedProduct() {
		UpdateProductDto request = new UpdateProductDto();
		request.setName("Updated Phone");
		request.setPrice(new BigDecimal("899.99"));
		ProductDto dto = productDto(11L, 3L, 7L, "Updated Phone");
		when(productService.updateProduct(7L, 11L, request)).thenReturn(dto);

		ResponseEntity<ApiResponse<ProductDto>> response = controller.updateProduct(7L, 11L, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(dto, response.getBody().getData());
		verify(productService).updateProduct(7L, 11L, request);
	}

	@Test
	void deleteProductInvokesServiceAndReturnsNoContent() {
		ResponseEntity<Void> response = controller.deleteProduct(7L, 11L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertNull(response.getBody());
		verify(productService).deleteProduct(7L, 11L);
	}

	@Test
	void updateProductPropagatesServiceFailure() {
		UpdateProductDto request = new UpdateProductDto();
		RuntimeException expected = new RuntimeException("seller does not own product");
		when(productService.updateProduct(7L, 11L, request)).thenThrow(expected);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> controller.updateProduct(7L, 11L, request));

		assertSame(expected, thrown);
		verify(productService).updateProduct(7L, 11L, request);
	}

	private CreateProductRequest createProductRequest() {
		CreateProductRequest request = new CreateProductRequest();
		request.setName("Phone");
		request.setDescription("Flagship phone");
		request.setPrice(new BigDecimal("999.99"));
		request.setStockQuantity(10);
		request.setCategoryId(3L);
		return request;
	}

	private ProductDto productDto(Long id, Long categoryId, Long sellerId, String name) {
		return ProductDto.builder()
				.id(id)
				.name(name)
				.description(name + " description")
				.price(new BigDecimal("999.99"))
				.stockQuantity(10)
				.categoryId(categoryId)
				.sellerId(sellerId)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
	}
}


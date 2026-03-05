package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CreateProductRequest;
import com.springboot.ecommerce.dtos.ProductDto;
import com.springboot.ecommerce.dtos.UpdateProductDto;
import com.springboot.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
@AllArgsConstructor
public class ProductController {
	private ProductService productService;

	@PostMapping
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<ApiResponse<ProductDto>> createProduct(
			@AuthenticationPrincipal Long userId,
			@Valid @RequestBody CreateProductRequest dto) {
		ApiResponse<ProductDto> response = new ApiResponse<>(
				true,
				null,
				productService.createProduct(userId, dto));

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDto>> getById(@PathVariable Long productId) {
		ApiResponse<ProductDto> response = new ApiResponse<>(
				true,
				null,
				productService.getProductById(productId));

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts() {
		ApiResponse<List<ProductDto>> response = new ApiResponse<>(
				true,
				null,
				productService.getAllProducts()
		);

		return ResponseEntity.ok(response);
	}

	@GetMapping(params = "category")
	public ResponseEntity<ApiResponse<List<ProductDto>>> getAllCategoryProducts(@RequestParam(name = "category") Long categoryId) {
		ApiResponse<List<ProductDto>> response = new ApiResponse<>(
				true,
				null,
				productService.getProductsByCategory(categoryId)
		);

		return ResponseEntity.ok(response);
	}

	@GetMapping(params = "seller")
	public ResponseEntity<ApiResponse<List<ProductDto>>> getAllSellerProducts(@RequestParam(name = "seller") Long sellerId) {
		ApiResponse<List<ProductDto>> response = new ApiResponse<>(
				true,
				null,
				productService.getProductsBySeller(sellerId)
		);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{productId}")
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
			@AuthenticationPrincipal Long userId,
			@PathVariable Long productId,
			@RequestBody UpdateProductDto dto) {
		ApiResponse<ProductDto> response = new ApiResponse<>(
				true,
				null,
				productService.updateProduct(userId, productId, dto));

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{productId}")
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<Void> deleteProduct(
			@AuthenticationPrincipal Long userId,
			@PathVariable Long productId) {
		productService.deleteProduct(userId, productId);

		return ResponseEntity.noContent().build();
	}

}

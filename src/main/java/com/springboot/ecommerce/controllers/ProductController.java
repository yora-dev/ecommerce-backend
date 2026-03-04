package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CreateProductRequest;
import com.springboot.ecommerce.dtos.ProductDto;
import com.springboot.ecommerce.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
@AllArgsConstructor
public class ProductController {
	private ProductService productService;

	@PostMapping
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<ApiResponse<ProductDto>> createProduct(
			@AuthenticationPrincipal Long userId,
			@RequestBody CreateProductRequest dto) {
		ApiResponse<ProductDto> response = new ApiResponse<>(
				true,
				null,
				productService.createProduct(userId, dto));

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}

package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.AddToCartRequest;
import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CartDto;
import com.springboot.ecommerce.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/carts")
@RestController
public class CartController {
	private CartService cartService;

	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<CartDto>> addToCart(
			@AuthenticationPrincipal Long userId,
			@Valid @RequestBody AddToCartRequest dto
	) {
		ApiResponse<CartDto> response = new ApiResponse<>(
				true,
				null,
				cartService.addToCart(userId, dto));

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}


}

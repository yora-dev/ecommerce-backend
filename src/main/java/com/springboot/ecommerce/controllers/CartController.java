package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.AddToCartRequest;
import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CartDto;
import com.springboot.ecommerce.dtos.CartItemDto;
import com.springboot.ecommerce.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping
	public ResponseEntity<ApiResponse<CartDto>> getCart(
			@AuthenticationPrincipal Long userId
	) {
		ApiResponse<CartDto> response = new ApiResponse<>(
				true,
				null,
				cartService.getCart(userId));

		return ResponseEntity.ok(response);
	}

	@GetMapping("/items/{cartItemId}")
	public ResponseEntity<ApiResponse<CartItemDto>> getCartItem(
			@AuthenticationPrincipal Long userId,
			@PathVariable Long cartItemId
	) {
		ApiResponse<CartItemDto> response = new ApiResponse<>(
				true,
				null,
				cartService.getCartItem(userId, cartItemId));

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/items/{productId}")
	public ResponseEntity<ApiResponse<CartDto>> removeFromCart(
			@AuthenticationPrincipal Long userId,
			@PathVariable Long productId
	) {
		ApiResponse<CartDto> response = new ApiResponse<>(
				true,
				null,
				cartService.removeFromCart(userId, productId));

		return ResponseEntity.ok(response);
	}

	@PutMapping("/items/{productId}")
	public ResponseEntity<ApiResponse<CartDto>> updateCartItemQuantity(
			@AuthenticationPrincipal Long userId,
			@PathVariable Long productId,
			@RequestParam Integer quantity
	) {
		ApiResponse<CartDto> response = new ApiResponse<>(
				true,
				null,
				cartService.updateCartItemQuantity(userId, productId, quantity));

		return ResponseEntity.ok(response);
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse<Void>> clearCart(
			@AuthenticationPrincipal Long userId
	) {
		cartService.clearCart(userId);
		return ResponseEntity.noContent().build();
	}


}

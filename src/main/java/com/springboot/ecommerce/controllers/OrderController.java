package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor

public class OrderController {
	private OrderService orderService;

	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderDto>> createOrder(
			@AuthenticationPrincipal Long userId) {
		ApiResponse<OrderDto> response = new ApiResponse<>(
				true,
				null,
				orderService.placeOrder(userId)
		);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping
	public ResponseEntity<ApiResponse<List<OrderDto>>> getAllOrderForUser(
			@AuthenticationPrincipal Long userId) {
		ApiResponse<List<OrderDto>> response = new ApiResponse<>(
				true,
				null,
				orderService.getAllOrdersForUser(userId)
		);

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<OrderDto>> getOrderById(
			@AuthenticationPrincipal Long userId,
			@PathVariable Long orderId) {
		ApiResponse<OrderDto> response = new ApiResponse<>(
				true,
				null,
				orderService.getOrderById(userId, orderId)
		);

		return ResponseEntity.ok(response);
	}
}

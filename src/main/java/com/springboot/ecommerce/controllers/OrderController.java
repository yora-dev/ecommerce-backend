package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.dtos.OrderItemDto;
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

	@PreAuthorize("hasRole('SELLER')")
	@GetMapping("/seller")
	public ResponseEntity<ApiResponse<List<OrderItemDto>>> getSellerOrdersForProduct(
			@AuthenticationPrincipal Long sellerId) {
		ApiResponse<List<OrderItemDto>> response = new ApiResponse<>(
				true,
				null,
				orderService.getSellerOrders(sellerId));

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasRole('SELLER')")
	@GetMapping("/seller/{productId}")
	public ResponseEntity<ApiResponse<List<OrderDto>>> getSellerOrdersForProduct(
			@AuthenticationPrincipal Long sellerId,
			@PathVariable Long productId) {
		ApiResponse<List<OrderDto>> response = new ApiResponse<>(
				true,
				null,
				orderService.getSellerOrdersForProduct(sellerId, productId));

		return ResponseEntity.ok(response);
	}

	@PutMapping(params = {"id", "status"})
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<ApiResponse<OrderItemDto>> updateOrderItemStatus(
			@AuthenticationPrincipal Long userId,
			@RequestParam(name = "id") Long orderItemId,
			@RequestParam(name = "status") String status) {
		ApiResponse<OrderItemDto> response = new ApiResponse<>(
				true,
				null,
				orderService.updateOrderItemStatus(userId, orderItemId, status)
		);

		return ResponseEntity.ok(response);
	}

	@PutMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderItemDto>> cancelOrder(
			@AuthenticationPrincipal Long userId,
			@RequestParam(name = "id") Long orderItemId) {
		ApiResponse<OrderItemDto> response = new ApiResponse<>(
				true,
				null,
				orderService.cancelOrder(userId, orderItemId)
		);

		return ResponseEntity.ok(response);
	}
}

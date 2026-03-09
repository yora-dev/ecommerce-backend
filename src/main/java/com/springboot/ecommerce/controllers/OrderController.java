package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor

public class OrderController {
	private OrderService orderService;

	@PostMapping
	public ResponseEntity<ApiResponse<OrderDto>> createOrder(
			@AuthenticationPrincipal Long userId) {
		ApiResponse<OrderDto> response = new ApiResponse<>(
				true,
				null,
				orderService.createOrder(userId)
		);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}

package com.springboot.ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
	@NotNull(message = "Product ID is required")
	private Long productId;

	private Integer quantity;
}


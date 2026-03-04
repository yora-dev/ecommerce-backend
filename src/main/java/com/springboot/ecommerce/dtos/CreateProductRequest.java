package com.springboot.ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class CreateProductRequest {
	@NotNull(message = "Product name is required")
	private String name;

	@NotNull(message = "Product description is required")
	private String description;

	@NotNull(message = "Product price is required")
	@PositiveOrZero
	private BigDecimal price;

	@NotNull(message = "Stock quantity is required")
	@PositiveOrZero
	private Integer stockQuantity;

	@NotNull(message = "Category ID is required")
	private Long categoryId;
}

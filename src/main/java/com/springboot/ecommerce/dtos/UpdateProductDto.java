package com.springboot.ecommerce.dtos;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductDto {
	private String name;
	private String description;
	@PositiveOrZero
	private BigDecimal price;
	@PositiveOrZero
	private Integer stockQuantity;
	private Long categoryId;
}


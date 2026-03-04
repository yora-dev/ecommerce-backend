package com.springboot.ecommerce.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
public class ProductDto {
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stockQuantity;
	private Long categoryId;
	private Long sellerId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

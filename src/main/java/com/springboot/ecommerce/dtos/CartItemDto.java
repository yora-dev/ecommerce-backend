package com.springboot.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class CartItemDto {
	private Long id;
	private Long productId;
	private Integer quantity;
}

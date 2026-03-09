package com.springboot.ecommerce.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
	private Long id;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private BigDecimal totalPrice;
	private List<OrderItemDto> items;
}

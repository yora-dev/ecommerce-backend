package com.springboot.ecommerce.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItemDto {

	private Long id;
	private Long orderId;
	private Long productId;
	private Integer quantity;
	private BigDecimal price;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

package com.springboot.ecommerce.mappers;

import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.dtos.OrderItemDto;
import com.springboot.ecommerce.entities.Order;
import com.springboot.ecommerce.entities.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
	OrderDto toDto(Order order) {
		OrderDto dto = new OrderDto();
		dto.setId(order.getId());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setUpdatedAt(order.getUpdatedAt());
		dto.setTotalPrice(order.getTotalPrice());
		dto.setItems(order.getItems().stream().map(this::toItemDto).collect(Collectors.toList()));
		return dto;
	}

	OrderItemDto toItemDto(OrderItem item) {
		OrderItemDto dto = new OrderItemDto();
		dto.setId(item.getId());
		dto.setOrderId(item.getOrder().getId());
		dto.setProductId(item.getProduct().getId());
		dto.setQuantity(item.getQuantity());
		dto.setPrice(item.getPriceAtOrder());
		dto.setStatus(String.valueOf(item.getStatus()));
		dto.setCreatedAt(item.getCreatedAt());
		dto.setUpdatedAt(item.getUpdatedAt());
		return dto;
	}
}

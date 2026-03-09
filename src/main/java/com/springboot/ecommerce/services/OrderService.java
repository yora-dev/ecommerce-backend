package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.dtos.OrderItemDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
	OrderDto placeOrder(Long userId);
	List<OrderDto> getAllOrdersForUser(Long userId);
	OrderDto getOrderById(Long userId, Long orderId);
	List<OrderItemDto> getSellerOrdersForProduct(Long sellerId, Long productId);
	OrderDto updateOrderItemStatus(Long userId, Long orderItemId, String status);
	void cancelOrder(Long userId, Long orderId);
}

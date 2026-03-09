package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.dtos.OrderItemDto;
import com.springboot.ecommerce.entities.*;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.mappers.OrderMapper;
import com.springboot.ecommerce.repositories.CartRepository;
import com.springboot.ecommerce.repositories.OrderItemRepository;
import com.springboot.ecommerce.repositories.OrderRepository;
import com.springboot.ecommerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor

public class OrderServiceImpl implements OrderService{
	private OrderMapper orderMapper;
	private OrderRepository orderRepository;
	private OrderItemRepository orderItemRepository;
	private UserRepository userRepository;
	private CartRepository cartRepository;
	private CartService cartService;

	@Override
	@Transactional
	public OrderDto placeOrder(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		if (!user.isCustomer()) {
			throw new IllegalArgumentException("Only customers can place orders");
		}

		if (!user.hasCart()) {
			throw new IllegalStateException("User has no cart to place an order");
		}

		Cart cart = user.getCart();

		if (cart.getItems().isEmpty()) {
			throw new IllegalStateException("Cart is empty, cannot place order");
		}

		Order order = new Order();

		order.setCreatedAt(LocalDateTime.now());
		order.setCustomer(user);

		List<CartItem> items = cart.getItems();

		orderRepository.save(order);

		List<OrderItem> orderItems = new ArrayList<>(
				items.stream()
						.map(item -> createOrderItemfromCartItem(item, order))
						.toList()
		);

		order.setItems(orderItems);

		orderItemRepository.saveAll(orderItems);
		order.setTotalPrice(order.calculateTotalAmount());

		orderRepository.save(order);

		cartService.clearCart(userId);

		return orderMapper.toDto(order);
	}

	@Override
	public List<OrderDto> getAllOrdersForUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		if (!user.isCustomer()) {
			throw new RuntimeException("Only users have orders");
		}

		var orders = orderRepository.findAllByCustomerId(userId);

		return orders
				.stream()
				.map(orderMapper::toDto)
				.toList();
	}

	@Override
	public OrderDto getOrderById(Long userId, Long orderId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		if (!user.isCustomer()) {
			throw new RuntimeException("Only users have orders");
		}

		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		if (!order.getCustomer().getId().equals(userId)) {
			throw new RuntimeException("User does not have access to this order");
		}

		return orderMapper.toDto(order);
	}

	@Override
	public List<OrderItemDto> getSellerOrdersForProduct(Long sellerId, Long productId) {
		return List.of();
	}

	@Override
	public OrderDto updateOrderItemStatus(Long userId, Long orderItemId, String status) {
		return null;
	}

	@Override
	public void cancelOrder(Long userId, Long orderId) {
	}

	private OrderItem createOrderItemfromCartItem(CartItem cartItem, Order order) {
		OrderItem item = new OrderItem();
		item.setProduct(cartItem.getProduct());
		item.setCreatedAt(LocalDateTime.now());
		item.setStatus(OrderStatus.PENDING);
		item.setOrder(order);
		item.setQuantity(cartItem.getQuantity());
		item.setPriceAtOrder(cartItem.getProduct().getPrice());

		return item;
	}
}

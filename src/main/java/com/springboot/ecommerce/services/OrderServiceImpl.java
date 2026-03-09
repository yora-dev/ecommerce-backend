package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.OrderDto;
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
	public OrderDto createOrder(Long userId) {
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

package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.entities.Cart;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.mappers.OrderMapper;
import com.springboot.ecommerce.repositories.CartRepository;
import com.springboot.ecommerce.repositories.OrderRepository;
import com.springboot.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class OrderServiceImpl implements OrderService{
	private OrderMapper orderMapper;
	private OrderRepository orderRepository;
	private UserRepository userRepository;
	private CartRepository cartRepository;
	private CartService cartService;

	@Override
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



		return null;
	}
}

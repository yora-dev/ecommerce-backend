package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.dtos.OrderItemDto;
import com.springboot.ecommerce.entities.*;
import com.springboot.ecommerce.exceptions.OrderItemNotFoundException;
import com.springboot.ecommerce.exceptions.ProductNotFoundException;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.mappers.OrderMapper;
import com.springboot.ecommerce.repositories.*;
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
	private ProductRepository productRepository;
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
						.map(item -> createOrderItemFromCartItem(item, order))
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
	@Transactional
	public List<OrderItemDto> getSellerOrders(Long sellerId) {
		User user = userRepository.findById(sellerId).orElseThrow(UserNotFoundException::new);
		if (!user.isSeller()) {
			throw new RuntimeException("Only sellers have access to this endpoint");
		}

		List<Product> products = productRepository.findAllBySellerId(sellerId);

		if (products.isEmpty()) {
			throw new RuntimeException("This Seller has no products");
		}

		List<OrderItem> items = new ArrayList<>();
		for (Product product : products) {
			items.addAll(orderItemRepository.findAllByProductId(product.getId()));
		}

		return items.stream().map(orderMapper::toItemDto).toList();
	}

	@Override
	public List<OrderDto> getSellerOrdersForProduct(Long sellerId, Long productId) {
		User user = userRepository.findById(sellerId).orElseThrow(UserNotFoundException::new);
		if (!user.isSeller()) {
			throw new RuntimeException("Only sellers have access to this endpoint");
		}
		Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

		if(!product.getSeller().getId().equals(sellerId)) {
			throw new RuntimeException("Seller does not have access to this product");
		}

		List<OrderItem> items = orderItemRepository.findAllByProductId(productId);

		return items.stream().map(item -> orderMapper.toDto(item.getOrder())).toList();
	}

	@Override
	public OrderItemDto updateOrderItemStatus(Long userId, Long orderItemId, String status) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		if (!user.isSeller()) {
			throw new RuntimeException("Only sellers can update order item status");
		}

		OrderItem item = orderItemRepository
				.findById(orderItemId)
				.orElseThrow(OrderItemNotFoundException::new);

		if (!item.getProduct().getSeller().getId().equals(userId)) {
			throw new RuntimeException("Seller does not have access to this order item");
		}

		 try {
			 OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
			 item.setStatus(newStatus);
			 item.setUpdatedAt(LocalDateTime.now());
			 orderItemRepository.save(item);
		 } catch (IllegalArgumentException e) {
			 throw new RuntimeException("Invalid status value");
		 }

		 item.getOrder().setUpdatedAt(LocalDateTime.now());
		 orderRepository.save(item.getOrder());
		 return orderMapper.toItemDto(item);
	}

	@Override
	public OrderItemDto cancelOrder(Long userId, Long orderItemId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		if (!user.isCustomer()) {
			throw new RuntimeException("Only customers can cancel orders");
		}

		OrderItem item = orderItemRepository
				.findById(orderItemId)
				.orElseThrow(OrderItemNotFoundException::new);

		if (!item.getOrder().getCustomer().getId().equals(userId)) {
			throw new RuntimeException("Customer does not have access to this order item");
		}

		item.setStatus(OrderStatus.CANCELLED);
		item.setUpdatedAt(LocalDateTime.now());
		orderItemRepository.save(item);

		item.getOrder().setUpdatedAt(LocalDateTime.now());
		orderRepository.save(item.getOrder());

		return orderMapper.toItemDto(item);
	}

	private OrderItem createOrderItemFromCartItem(CartItem cartItem, Order order) {
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

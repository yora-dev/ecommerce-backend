package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.AddToCartRequest;
import com.springboot.ecommerce.dtos.CartDto;
import com.springboot.ecommerce.dtos.CartItemDto;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
	CartDto addToCart(Long userId, AddToCartRequest dto);

	CartDto getCart(Long userId);

	CartItemDto getCartItem(Long userId, Long cartItemId);

	void removeFromCart(Long userId, Long cartId, Long productId);

	void updateCartItem(Long userId, Long cartId, Long productId, Integer quantity);

	void clearCart(Long userId, Long cartId);
}

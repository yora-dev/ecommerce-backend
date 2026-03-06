package com.springboot.ecommerce.mappers;

import com.springboot.ecommerce.dtos.CartDto;
import com.springboot.ecommerce.dtos.CartItemDto;
import com.springboot.ecommerce.entities.Cart;
import com.springboot.ecommerce.entities.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {
	public CartDto toCartDto(Cart cart) {
		CartDto cartDto = new CartDto();
		cartDto.setId(cart.getId());
		cartDto.setUserId(cart.getUser().getId());
		cartDto.setCreatedAt(cart.getCreatedAt());
		cartDto.setUpdatedAt(cart.getUpdatedAt());

		cartDto.setItems(cart.getItems().stream()
				.map(this::toCartItemDto)
				.toList());

		return cartDto;
	}

	public CartItemDto toCartItemDto(CartItem cartItem) {
		return new CartItemDto(
				cartItem.getId(),
				cartItem.getProduct().getId(),
				cartItem.getQuantity()
		);
	}
}

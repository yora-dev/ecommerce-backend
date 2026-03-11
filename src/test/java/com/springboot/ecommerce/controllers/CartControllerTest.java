package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.AddToCartRequest;
import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.CartDto;
import com.springboot.ecommerce.dtos.CartItemDto;
import com.springboot.ecommerce.services.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

	@Mock
	private CartService cartService;
	@InjectMocks
	private CartController controller;

	@Test
	void addToCartReturnsCreatedCart() {
		AddToCartRequest request = new AddToCartRequest();
		request.setProductId(15L);
		request.setQuantity(2);
		CartDto cart = cartDto(1L, 4L);
		when(cartService.addToCart(4L, request)).thenReturn(cart);

		ResponseEntity<ApiResponse<CartDto>> response = controller.addToCart(4L, request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isSuccess());
		assertEquals(cart, response.getBody().getData());
		verify(cartService).addToCart(4L, request);
	}

	@Test
	void getCartReturnsCart() {
		CartDto cart = cartDto(1L, 4L);
		when(cartService.getCart(4L)).thenReturn(cart);

		ResponseEntity<ApiResponse<CartDto>> response = controller.getCart(4L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(cart, response.getBody().getData());
		verify(cartService).getCart(4L);
	}

	@Test
	void getCartItemReturnsCartItem() {
		CartItemDto item = new CartItemDto(8L, 15L, 2);
		when(cartService.getCartItem(4L, 8L)).thenReturn(item);

		ResponseEntity<ApiResponse<CartItemDto>> response = controller.getCartItem(4L, 8L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(item, response.getBody().getData());
		verify(cartService).getCartItem(4L, 8L);
	}

	@Test
	void removeFromCartReturnsUpdatedCart() {
		CartDto cart = cartDto(1L, 4L);
		when(cartService.removeFromCart(4L, 15L)).thenReturn(cart);

		ResponseEntity<ApiResponse<CartDto>> response = controller.removeFromCart(4L, 15L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(cart, response.getBody().getData());
		verify(cartService).removeFromCart(4L, 15L);
	}

	@Test
	void updateCartItemQuantityReturnsUpdatedCart() {
		CartDto cart = cartDto(1L, 4L);
		when(cartService.updateCartItemQuantity(4L, 15L, 3)).thenReturn(cart);

		ResponseEntity<ApiResponse<CartDto>> response = controller.updateCartItemQuantity(4L, 15L, 3);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(cart, response.getBody().getData());
		verify(cartService).updateCartItemQuantity(4L, 15L, 3);
	}

	@Test
	void clearCartInvokesServiceAndReturnsNoContent() {
		ResponseEntity<ApiResponse<Void>> response = controller.clearCart(4L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertNull(response.getBody());
		verify(cartService).clearCart(4L);
	}

	@Test
	void addToCartPropagatesServiceFailure() {
		AddToCartRequest request = new AddToCartRequest();
		RuntimeException expected = new RuntimeException("not enough stock");
		when(cartService.addToCart(4L, request)).thenThrow(expected);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> controller.addToCart(4L, request));

		assertSame(expected, thrown);
		verify(cartService).addToCart(4L, request);
	}

	private CartDto cartDto(Long cartId, Long userId) {
		CartDto cart = new CartDto();
		cart.setId(cartId);
		cart.setUserId(userId);
		cart.setItems(List.of(new CartItemDto(8L, 15L, 2)));
		cart.setCreatedAt(LocalDateTime.now());
		cart.setUpdatedAt(LocalDateTime.now());
		return cart;
	}
}


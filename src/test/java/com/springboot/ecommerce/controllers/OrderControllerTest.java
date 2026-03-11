package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.OrderDto;
import com.springboot.ecommerce.dtos.OrderItemDto;
import com.springboot.ecommerce.services.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

	@Mock
	private OrderService orderService;
	@InjectMocks
	private OrderController controller;

	@Test
	void createOrderReturnsCreatedOrder() {
		OrderDto order = orderDto(21L);
		when(orderService.placeOrder(4L)).thenReturn(order);

		ResponseEntity<ApiResponse<OrderDto>> response = controller.createOrder(4L);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isSuccess());
		assertEquals(order, response.getBody().getData());
		verify(orderService).placeOrder(4L);
	}

	@Test
	void getAllOrderForUserReturnsOrders() {
		List<OrderDto> orders = List.of(orderDto(21L), orderDto(22L));
		when(orderService.getAllOrdersForUser(4L)).thenReturn(orders);

		ResponseEntity<ApiResponse<List<OrderDto>>> response = controller.getAllOrderForUser(4L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(orders, response.getBody().getData());
		verify(orderService).getAllOrdersForUser(4L);
	}

	@Test
	void getOrderByIdReturnsOrder() {
		OrderDto order = orderDto(21L);
		when(orderService.getOrderById(4L, 21L)).thenReturn(order);

		ResponseEntity<ApiResponse<OrderDto>> response = controller.getOrderById(4L, 21L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(order, response.getBody().getData());
		verify(orderService).getOrderById(4L, 21L);
	}

	@Test
	void getSellerOrdersReturnsItems() {
		List<OrderItemDto> items = List.of(orderItemDto(31L, 11L));
		when(orderService.getSellerOrders(7L)).thenReturn(items);

		ResponseEntity<ApiResponse<List<OrderItemDto>>> response = controller.getSellerOrdersForProduct(7L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(items, response.getBody().getData());
		verify(orderService).getSellerOrders(7L);
	}

	@Test
	void getSellerOrdersForProductReturnsItems() {
		List<OrderItemDto> items = List.of(orderItemDto(31L, 11L));
		when(orderService.getSellerOrdersForProduct(7L, 11L)).thenReturn(items);

		ResponseEntity<ApiResponse<List<OrderItemDto>>> response = controller.getSellerOrdersForProduct(7L, 11L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(items, response.getBody().getData());
		verify(orderService).getSellerOrdersForProduct(7L, 11L);
	}

	@Test
	void updateOrderItemStatusReturnsUpdatedItem() {
		OrderItemDto item = orderItemDto(31L, 11L);
		item.setStatus("SHIPPED");
		when(orderService.updateOrderItemStatus(7L, 31L, "SHIPPED")).thenReturn(item);

		ResponseEntity<ApiResponse<OrderItemDto>> response = controller.updateOrderItemStatus(7L, 31L, "SHIPPED");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(item, response.getBody().getData());
		verify(orderService).updateOrderItemStatus(7L, 31L, "SHIPPED");
	}

	@Test
	void cancelOrderReturnsNoContent() {
		ResponseEntity<Void> response = controller.cancelOrder(4L, 21L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertNull(response.getBody());
		verify(orderService).cancelOrder(4L, 21L);
	}

	@Test
	void updateOrderItemStatusPropagatesServiceFailure() {
		RuntimeException expected = new RuntimeException("invalid status transition");
		when(orderService.updateOrderItemStatus(7L, 31L, "CANCELLED")).thenThrow(expected);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> controller.updateOrderItemStatus(7L, 31L, "CANCELLED"));

		assertSame(expected, thrown);
		verify(orderService).updateOrderItemStatus(7L, 31L, "CANCELLED");
	}

	private OrderDto orderDto(Long id) {
		OrderDto order = new OrderDto();
		order.setId(id);
		order.setCreatedAt(LocalDateTime.now());
		order.setUpdatedAt(LocalDateTime.now());
		order.setTotalPrice(new BigDecimal("120.00"));
		order.setItems(List.of(orderItemDto(id + 10, 11L)));
		return order;
	}

	private OrderItemDto orderItemDto(Long id, Long productId) {
		OrderItemDto item = new OrderItemDto();
		item.setId(id);
		item.setOrderId(21L);
		item.setProductId(productId);
		item.setQuantity(2);
		item.setPrice(new BigDecimal("60.00"));
		item.setStatus("PENDING");
		item.setCreatedAt(LocalDateTime.now());
		item.setUpdatedAt(LocalDateTime.now());
		return item;
	}
}


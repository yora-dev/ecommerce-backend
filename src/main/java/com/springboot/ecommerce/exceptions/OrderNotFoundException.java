package com.springboot.ecommerce.exceptions;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException(String message) {
		super(message);
	}
	public OrderNotFoundException() {
		super("Order not found");
	}
}

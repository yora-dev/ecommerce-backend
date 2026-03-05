package com.springboot.ecommerce.exceptions;

public class UnauthorizedAdminException extends RuntimeException {
	public UnauthorizedAdminException(String message) {
		super(message);
	}
}

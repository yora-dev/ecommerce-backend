package com.springboot.ecommerce.exceptions;

public class UnauthorizedSellerException extends RuntimeException {
	public UnauthorizedSellerException(String message) {
		super(message);
	}

}

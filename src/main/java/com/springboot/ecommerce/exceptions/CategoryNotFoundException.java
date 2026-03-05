package com.springboot.ecommerce.exceptions;

public class CategoryNotFoundException extends RuntimeException {
	public CategoryNotFoundException(String message) {
		super(message);
	}
    public CategoryNotFoundException() {
      super("Category not found.");
    }
}

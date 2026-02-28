package com.springboot.ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class ApiResponse<T> {
	private boolean success;
	private List<String> errors;
	private T data;
}

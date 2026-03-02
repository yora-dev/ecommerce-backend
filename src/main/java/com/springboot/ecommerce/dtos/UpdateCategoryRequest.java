package com.springboot.ecommerce.dtos;

import lombok.Data;

@Data
public class UpdateCategoryRequest {
	private String name;

	private String description;
}

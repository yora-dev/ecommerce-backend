package com.springboot.ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryRequest {
	@NotNull(message = "Category name is required")
	private String name;

	@NotNull(message = "Category description is required")
	private String description;

}

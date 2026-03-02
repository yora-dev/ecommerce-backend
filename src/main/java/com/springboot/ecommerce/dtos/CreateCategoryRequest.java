package com.springboot.ecommerce.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCategoryRequest {
	@NotNull(message = "Name is required")
	private String name;

	@NotNull(message = "Description is required")
	private String description;

}

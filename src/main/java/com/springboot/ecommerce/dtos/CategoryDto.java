package com.springboot.ecommerce.dtos;

import com.springboot.ecommerce.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
	private Long categoryId;
	private String name;
	private String description;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public CategoryDto toDto(Category category) {
		return CategoryDto.builder()
				.name(category.getName())
				.description(category.getDescription())
				.createdAt(category.getCreatedAt())
				.updatedAt(category.getUpdatedAt())
				.build();
	}

}

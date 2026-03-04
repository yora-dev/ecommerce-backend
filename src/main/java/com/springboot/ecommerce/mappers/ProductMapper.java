package com.springboot.ecommerce.mappers;

import com.springboot.ecommerce.dtos.CreateProductRequest;
import com.springboot.ecommerce.dtos.ProductDto;
import com.springboot.ecommerce.entities.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductMapper {
	public Product toEntity(CreateProductRequest dto) {
		return Product.builder()
				.name(dto.getName())
				.description(dto.getDescription())
				.price(dto.getPrice())
				.stockQuantity(dto.getStockQuantity())
				.createdAt(LocalDateTime.now())
				.build();
	}

	public ProductDto toDto(Product product) {
		return ProductDto.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.stockQuantity(product.getStockQuantity())
				.categoryId(product.getCategory().getId())
				.sellerId(product.getSeller().getId())
				.createdAt(product.getCreatedAt())
				.updatedAt(product.getUpdatedAt())
				.build();
	}
}

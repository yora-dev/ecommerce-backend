package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.CreateProductRequest;
import com.springboot.ecommerce.dtos.ProductDto;
import com.springboot.ecommerce.dtos.UpdateProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
	ProductDto createProduct(Long userId, CreateProductRequest dto);
	ProductDto getProductById(Long productId);
	List<ProductDto> getAllProducts();
	List<ProductDto> getProductsByCategory(Long categoryId);
	List<ProductDto> getProductsBySeller(Long sellerId);
	ProductDto updateProduct(Long userId, Long productId, UpdateProductDto dto);
	void deleteProduct(Long userId, Long productId);
}

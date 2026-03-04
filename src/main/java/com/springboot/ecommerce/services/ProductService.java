package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.CreateProductRequest;
import com.springboot.ecommerce.dtos.ProductDto;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
	ProductDto createProduct(Long userId, CreateProductRequest dto);
}

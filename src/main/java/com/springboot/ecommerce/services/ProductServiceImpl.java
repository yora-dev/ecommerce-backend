package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.CreateProductRequest;
import com.springboot.ecommerce.dtos.ProductDto;
import com.springboot.ecommerce.entities.Category;
import com.springboot.ecommerce.entities.Product;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.exceptions.CategoryNotFoundException;
import com.springboot.ecommerce.exceptions.UnauthorizedSellerException;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.mappers.ProductMapper;
import com.springboot.ecommerce.repositories.CategoryRepository;
import com.springboot.ecommerce.repositories.ProductRepository;
import com.springboot.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
	private UserRepository userRepository;
	private ProductRepository productRepository;
	private CategoryRepository categoryRepository;
	private ProductMapper productMapper;

	@Override
	public ProductDto createProduct(Long userId, CreateProductRequest dto) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		if(!user.isSeller()) {
			throw new UnauthorizedSellerException("Only sellers can register product");
		}

		Category category = categoryRepository
				.findById(dto.getCategoryId())
				.orElseThrow(CategoryNotFoundException::new);

		Product product = productMapper.toEntity(dto);
		product.setCategory(category);
		product.setSeller(user);
		category.setUpdatedAt(LocalDateTime.now());
		categoryRepository.save(category);
		productRepository.save(product);

		return productMapper.toDto(product);
	}

}

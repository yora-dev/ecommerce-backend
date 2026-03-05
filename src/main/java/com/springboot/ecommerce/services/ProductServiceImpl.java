package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.CreateProductRequest;
import com.springboot.ecommerce.dtos.ProductDto;
import com.springboot.ecommerce.dtos.UpdateProductDto;
import com.springboot.ecommerce.entities.Category;
import com.springboot.ecommerce.entities.Product;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.exceptions.*;
import com.springboot.ecommerce.mappers.ProductMapper;
import com.springboot.ecommerce.repositories.CategoryRepository;
import com.springboot.ecommerce.repositories.ProductRepository;
import com.springboot.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

	@Override
	public ProductDto getProductById(Long productId) {
		Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

		return productMapper.toDto(product);
	}

	@Override
	public List<ProductDto> getAllProducts() {
		var products = productRepository.findAll();
		return products.stream().map(productMapper::toDto).toList();
	}

	@Override
	public List<ProductDto> getProductsByCategory(Long categoryId) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new CategoryNotFoundException();
		}

		var products = productRepository.findAllByCategoryId(categoryId);
		return products.stream().map(productMapper::toDto).toList();
	}

	@Override
	public List<ProductDto> getProductsBySeller(Long sellerId) {
		if (!userRepository.existsById(sellerId)) {
			throw new UserNotFoundException();
		}
		var products = productRepository.findAllBySellerId(sellerId);

		return products.stream().map(productMapper::toDto).toList();
	}

	@Override
	public ProductDto updateProduct(Long userId, Long productId, UpdateProductDto dto) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		if (!user.isSeller()) {
			throw new UnauthorizedSellerException("Only sellers can update a product.");
		}

		Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

		if(!userId.equals(product.getSeller().getId())) {
			throw new UnauthorizedSellerException("You can only update your product");
		}

		if(dto.getCategoryId() != null) {
			Category category = categoryRepository
					.findById(dto.getCategoryId())
					.orElseThrow(CategoryNotFoundException::new);
			product.setCategory(category);
		}

		if(dto.getName() != null) {
			product.setName(dto.getName());
		}
		if(dto.getDescription() != null) {
			product.setDescription(dto.getDescription());
		}
		if(dto.getPrice() != null) {
			product.setPrice(dto.getPrice());
		}

		if(dto.getStockQuantity() != null) {
			product.setStockQuantity(dto.getStockQuantity());
		}
		product.setUpdatedAt(LocalDateTime.now());

		return productMapper.toDto(productRepository.save(product));
	}

	@Override
	public void deleteProduct(Long userId, Long productId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		if (!user.isSeller()) {
			throw new UnauthorizedSellerException("Only sellers can delete a product.");
		}

		Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

		if(!userId.equals(product.getSeller().getId())) {
			throw new UnauthorizedSellerException("You can only delete your product.");
		}

		productRepository.delete(product);
	}

}

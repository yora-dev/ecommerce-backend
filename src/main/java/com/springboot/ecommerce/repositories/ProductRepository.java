package com.springboot.ecommerce.repositories;

import com.springboot.ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findAllByCategoryId(Long categoryId);
	List<Product> findAllBySellerId(Long sellerId);

}

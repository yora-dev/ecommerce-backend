package com.springboot.ecommerce.repositories;

import com.springboot.ecommerce.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	Optional<OrderItem> findByProductId(Long productId);
	List<OrderItem> findAllByProductId(Long sellerId);
}

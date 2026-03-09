package com.springboot.ecommerce.repositories;

import com.springboot.ecommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findAllByCustomerId(Long customerId);
}

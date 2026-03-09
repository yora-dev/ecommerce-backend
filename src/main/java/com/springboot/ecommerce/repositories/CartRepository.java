package com.springboot.ecommerce.repositories;

import com.springboot.ecommerce.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}

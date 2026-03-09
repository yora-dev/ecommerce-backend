package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.OrderDto;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
	OrderDto createOrder(Long userId);

}

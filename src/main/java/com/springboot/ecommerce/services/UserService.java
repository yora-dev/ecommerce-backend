package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.SellerProfileDto;
import com.springboot.ecommerce.dtos.UpgradeToSellerRequest;
import com.springboot.ecommerce.dtos.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
	UserDto getUserById(Long userId);
	SellerProfileDto upgradeToSeller(Long userId, UpgradeToSellerRequest dto);

}

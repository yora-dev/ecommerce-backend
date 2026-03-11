package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.LoginDto;
import com.springboot.ecommerce.dtos.RegisterUserRequest;
import com.springboot.ecommerce.dtos.UserDto;
import com.springboot.ecommerce.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
	UserDto registerUser(RegisterUserRequest registerRequest);
	User login(LoginDto loginRequest);
}

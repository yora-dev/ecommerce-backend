package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.RegisterUserRequest;
import com.springboot.ecommerce.dtos.LoginDto;
import com.springboot.ecommerce.dtos.UserDto;
import com.springboot.ecommerce.entities.Role;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.exceptions.DuplicateResourceException;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.mappers.UserMapper;
import com.springboot.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
	private UserRepository userRepository;
	private UserMapper userMapper;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;

	public UserDto registerUser(RegisterUserRequest registerRequest) {
		if(userRepository.existsByEmail(registerRequest.getEmail())) {
			throw new DuplicateResourceException("The email is already in use");
		}

		if(userRepository.existsByUsername(registerRequest.getUsername())) {
			throw new RuntimeException("The user already exists");
		}

		User user = userMapper.toEntity(registerRequest);
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setRole(Role.CUSTOMER);
		userRepository.save(user);

		return userMapper.toDto(user);
	}

	public User login(LoginDto loginRequest) {

		User user = userRepository.findByUsername(loginRequest.getUsername())
				.orElseThrow(UserNotFoundException::new);

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(),
						loginRequest.getPassword()
				)
		);

		return user;
	}



}

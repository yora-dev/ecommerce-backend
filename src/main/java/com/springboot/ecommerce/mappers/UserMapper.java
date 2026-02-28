package com.springboot.ecommerce.mappers;

import com.springboot.ecommerce.dtos.RegisterUserRequest;
import com.springboot.ecommerce.dtos.UserDto;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public class UserMapper {
	private PasswordEncoder passwordEncoder;

	public User toEntity(RegisterUserRequest request) {

		return User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.createdAt(LocalDateTime.now())
				.build();
	}

	public UserDto toDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setEmail(user.getEmail());
		userDto.setRole(user.getRole());
		return userDto;
	}

}

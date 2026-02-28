package com.springboot.ecommerce.mappers;

import com.springboot.ecommerce.dtos.RegisterUserRequest;
import com.springboot.ecommerce.dtos.UserDto;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@AllArgsConstructor
@Component
public class UserMapper {
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;

	public User toEntity(RegisterUserRequest request) {

		return User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(roleRepository.findByName("CUSTOMER"))
				.build();
	}

	public UserDto toDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setEmail(user.getEmail());
		userDto.setRole(user.getRole().getName());
		return userDto;
	}

}

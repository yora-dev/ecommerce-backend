package com.springboot.ecommerce.dtos;

import com.springboot.ecommerce.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long id;
	private String username;
	private String email;
	private Role role;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

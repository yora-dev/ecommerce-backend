package com.springboot.ecommerce.dtos;

import com.springboot.ecommerce.entities.Role;
import lombok.Data;

@Data
public class UserDto {
	private Long id;
	private String username;
	private String email;
	private Role role;
}

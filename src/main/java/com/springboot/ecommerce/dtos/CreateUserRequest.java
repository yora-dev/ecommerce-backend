package com.springboot.ecommerce.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
	@NotNull(message = "Username is required")
	private String username;

	@NotNull(message = "Email is required")
	@Email
	private String email;

	@NotNull(message = "Password is required")
	private String password;

}

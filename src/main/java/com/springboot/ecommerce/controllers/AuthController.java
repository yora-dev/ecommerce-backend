package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.config.JwtConfig;
import com.springboot.ecommerce.dtos.*;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.exceptions.DuplicateResourceException;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.repositories.UserRepository;
import com.springboot.ecommerce.services.AuthService;
import com.springboot.ecommerce.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private JwtConfig jwtConfig;

	private AuthService authService;
	private JwtService jwtService;
	private UserRepository userRepository;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody @Valid RegisterUserRequest registerUserDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
				true,
				null,
				authService.registerUser(registerUserDto)));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> authenticateUser(
			@RequestBody @Valid LoginDto loginDto,
			HttpServletResponse response
	) {
		String accessToken = jwtService.generateAccessToken(authService.login(loginDto));
		String refreshToken = jwtService.generateRefreshToken(authService.login(loginDto));

		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setHttpOnly(true);
		cookie.setPath("/auth/refresh");
		cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
		cookie.setSecure(true);
		response.addCookie(cookie);


		return ResponseEntity.ok(new ApiResponse<>(
				true,
				null,
				new LoginResponse(accessToken)));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
			@CookieValue(name = "refreshToken") String refreshToken
	) {
		if(jwtService.isTokenValid(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Long userId = jwtService.extractUserId(refreshToken);
		User user = userRepository.findById(userId).orElseThrow();
		String accessToken = jwtService.generateAccessToken(user);

		return ResponseEntity.ok(new ApiResponse<>(
				true,
				null,
				new LoginResponse(accessToken)));
	}

//	@GetMapping("/me")
//	public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(
//			@AuthenticationPrincipal Long userId) {
//		return ResponseEntity.ok(new ApiResponse<>(
//				true,
//				null,
//				authService.loggedInUser(userId)));
//	}



}

package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.SellerProfileDto;
import com.springboot.ecommerce.dtos.UpgradeToSellerRequest;
import com.springboot.ecommerce.dtos.UserDto;
import com.springboot.ecommerce.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
	private UserService userService;

	@PostMapping("/upgrade-to-seller")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<SellerProfileDto>> upgradeToSeller(
			@AuthenticationPrincipal Long userId,
			@RequestBody UpgradeToSellerRequest dto) {
		return ResponseEntity.ok(new ApiResponse<>(
				true,
				null,
				userService.upgradeToSeller(userId, dto)));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal Long userId) {

		return ResponseEntity.ok(new ApiResponse<>(
				true,
				null,
				userService.loggedInUser(userId)
		));
	}
}

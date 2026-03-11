package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.SellerProfileDto;
import com.springboot.ecommerce.dtos.UpgradeToSellerRequest;
import com.springboot.ecommerce.dtos.UserDto;
import com.springboot.ecommerce.entities.Role;
import com.springboot.ecommerce.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;
	@InjectMocks
	private UserController controller;

	@Test
	void upgradeToSellerReturnsSellerProfile() {
		UpgradeToSellerRequest request = new UpgradeToSellerRequest();
		request.setStoreName("Alice Store");
		request.setDescription("Curated picks");
		SellerProfileDto sellerProfile = sellerProfileDto();
		when(userService.upgradeToSeller(4L, request)).thenReturn(sellerProfile);

		ResponseEntity<ApiResponse<SellerProfileDto>> response = controller.upgradeToSeller(4L, request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isSuccess());
		assertEquals(sellerProfile, response.getBody().getData());
		verify(userService).upgradeToSeller(4L, request);
	}

	@Test
	void getCurrentUserReturnsLoggedInUser() {
		UserDto user = new UserDto(4L, "alice", "alice@example.com", Role.CUSTOMER, LocalDateTime.now(), LocalDateTime.now());
		when(userService.loggedInUser(4L)).thenReturn(user);

		ResponseEntity<ApiResponse<UserDto>> response = controller.getCurrentUser(4L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(user, response.getBody().getData());
		verify(userService).loggedInUser(4L);
	}

	@Test
	void upgradeToSellerPropagatesServiceFailure() {
		UpgradeToSellerRequest request = new UpgradeToSellerRequest();
		RuntimeException expected = new RuntimeException("user is already a seller");
		when(userService.upgradeToSeller(4L, request)).thenThrow(expected);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> controller.upgradeToSeller(4L, request));

		assertSame(expected, thrown);
		verify(userService).upgradeToSeller(4L, request);
	}

	private SellerProfileDto sellerProfileDto() {
		SellerProfileDto dto = new SellerProfileDto();
		dto.setId(12L);
		dto.setStoreName("Alice Store");
		dto.setDescription("Curated picks");
		dto.setUserId(4L);
		dto.setCreatedAt(LocalDateTime.now());
		return dto;
	}
}


package com.springboot.ecommerce.controllers;

import com.springboot.ecommerce.config.JwtConfig;
import com.springboot.ecommerce.dtos.ApiResponse;
import com.springboot.ecommerce.dtos.LoginDto;
import com.springboot.ecommerce.dtos.LoginResponse;
import com.springboot.ecommerce.dtos.RegisterUserRequest;
import com.springboot.ecommerce.dtos.UserDto;
import com.springboot.ecommerce.entities.Role;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.repositories.UserRepository;
import com.springboot.ecommerce.services.AuthService;
import com.springboot.ecommerce.services.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private JwtConfig jwtConfig;
	@Mock
	private AuthService authService;
	@Mock
	private JwtService jwtService;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private AuthController controller;

	@Test
	void createUserReturnsCreatedResponseWithRegisteredUser() {
		RegisterUserRequest request = new RegisterUserRequest();
		request.setUsername("alice");
		request.setEmail("alice@example.com");
		request.setPassword("P@ssw0rd");

		UserDto userDto = new UserDto(
				1L,
				"alice",
				"alice@example.com",
				Role.CUSTOMER,
				LocalDateTime.now(),
				LocalDateTime.now());

		when(authService.registerUser(request)).thenReturn(userDto);

		ResponseEntity<ApiResponse<UserDto>> response = controller.createUser(request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isSuccess());
		assertNull(response.getBody().getErrors());
		assertEquals(userDto, response.getBody().getData());
		verify(authService).registerUser(request);
		verifyNoInteractions(jwtService, userRepository, jwtConfig);
	}

	@Test
	void createUserPropagatesRegistrationFailure() {
		RegisterUserRequest request = new RegisterUserRequest();
		RuntimeException expected = new RuntimeException("email already used");
		when(authService.registerUser(request)).thenThrow(expected);

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> controller.createUser(request));

		assertSame(expected, thrown);
		verify(authService).registerUser(request);
	}

	@Test
	void authenticateUserSetsRefreshCookieAndReturnsAccessToken() {
		LoginDto loginDto = new LoginDto();
		loginDto.setUsername("alice");
		loginDto.setPassword("P@ssw0rd");

		User user = testUser(1L, "alice", Role.CUSTOMER);
		MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		when(authService.login(loginDto)).thenReturn(user);
		when(jwtService.generateAccessToken(user)).thenReturn("access-token");
		when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
		when(jwtConfig.getRefreshTokenExpiration()).thenReturn(3600);

		ResponseEntity<ApiResponse<LoginResponse>> response = controller.authenticateUser(loginDto, servletResponse);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isSuccess());
		assertNull(response.getBody().getErrors());
		assertEquals("access-token", response.getBody().getData().getToken());

		var cookie = servletResponse.getCookie("refreshToken");
		assertNotNull(cookie);
		assertEquals("refresh-token", cookie.getValue());
		assertTrue(cookie.isHttpOnly());
		assertTrue(cookie.getSecure());
		assertEquals("/auth/refresh", cookie.getPath());
		assertEquals(3600, cookie.getMaxAge());

		verify(authService, times(2)).login(loginDto);
		verify(jwtService).generateAccessToken(user);
		verify(jwtService).generateRefreshToken(user);
		verify(jwtConfig).getRefreshTokenExpiration();
	}

	@Test
	void authenticateUserPropagatesLoginFailureWithoutAddingCookie() {
		LoginDto loginDto = new LoginDto();
		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		RuntimeException expected = new RuntimeException("bad credentials");
		when(authService.login(loginDto)).thenThrow(expected);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> controller.authenticateUser(loginDto, servletResponse));

		assertSame(expected, thrown);
		assertNull(servletResponse.getCookie("refreshToken"));
		verify(authService).login(loginDto);
		verifyNoInteractions(jwtService, jwtConfig, userRepository);
	}

	@Test
	void refreshTokenReturnsUnauthorizedWhenTokenIsInvalid() {
		when(jwtService.isTokenValid("invalid-refresh-token")).thenReturn(false);

		ResponseEntity<ApiResponse<LoginResponse>> response = controller.refreshToken("invalid-refresh-token");

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNull(response.getBody());
		verify(jwtService).isTokenValid("invalid-refresh-token");
		verify(jwtService, never()).extractUserId(anyString());
		verifyNoInteractions(userRepository);
	}

	@Test
	void refreshTokenReturnsNewAccessTokenWhenTokenIsValid() {
		User user = testUser(9L, "seller", Role.SELLER);

		when(jwtService.isTokenValid("refresh-token")).thenReturn(true);
		when(jwtService.extractUserId("refresh-token")).thenReturn(9L);
		when(userRepository.findById(9L)).thenReturn(Optional.of(user));
		when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");

		ResponseEntity<ApiResponse<LoginResponse>> response = controller.refreshToken("refresh-token");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().isSuccess());
		assertNull(response.getBody().getErrors());
		assertEquals("new-access-token", response.getBody().getData().getToken());
		verify(jwtService).isTokenValid("refresh-token");
		verify(jwtService).extractUserId("refresh-token");
		verify(userRepository).findById(9L);
		verify(jwtService).generateAccessToken(user);
	}

	@Test
	void refreshTokenThrowsWhenUserDoesNotExist() {
		when(jwtService.isTokenValid("refresh-token")).thenReturn(true);
		when(jwtService.extractUserId("refresh-token")).thenReturn(99L);
		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(java.util.NoSuchElementException.class, () -> controller.refreshToken("refresh-token"));

		verify(jwtService).isTokenValid("refresh-token");
		verify(jwtService).extractUserId("refresh-token");
		verify(userRepository).findById(99L);
		verify(jwtService, never()).generateAccessToken(any());
	}

	private User testUser(Long id, String username, Role role) {
		return User.builder()
				.id(id)
				.username(username)
				.email(username + "@example.com")
				.role(role)
				.build();
	}
}

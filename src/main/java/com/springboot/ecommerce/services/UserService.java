package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.SellerProfileDto;
import com.springboot.ecommerce.dtos.UpgradeToSellerRequest;
import com.springboot.ecommerce.dtos.UserDto;
import com.springboot.ecommerce.entities.Role;
import com.springboot.ecommerce.entities.SellerProfile;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.mappers.UserMapper;
import com.springboot.ecommerce.repositories.SellerProfileRepository;
import com.springboot.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final SellerProfileRepository sellerProfileRepository;
	private final UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException("User not found"));

		return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
	}

	public UserDto getUserById(Long userId) {
		var user = userRepository.findById(userId).orElseThrow(
				UserNotFoundException::new);

		return userMapper.toDto(user);
	}

	public SellerProfileDto upgradeToSeller(Long userId, UpgradeToSellerRequest dto) {
		var user = userRepository.findById(userId).orElseThrow(
				UserNotFoundException::new);

		if(!Objects.equals(user.getId(), dto.getUserId()))
			throw new IllegalStateException("Forbidden: You can only upgrade your own account");

		if(!user.isCustomer())
			throw new IllegalStateException("Only customers can upgrade to sellers");

		var sellerProfile = new SellerProfile();
		sellerProfile.setStoreName(dto.getStoreName());
		sellerProfile.setDescription(dto.getDescription());
		sellerProfile.setUser(user);
		sellerProfile.setCreatedAt(LocalDateTime.now());
		sellerProfileRepository.save(sellerProfile);

		user.setRole(Role.SELLER);
		userRepository.save(user);

		return new SellerProfileDto().toDto(sellerProfile);
	}

	public UserDto loggedInUser(Long userId) {
		var user = userRepository.findById(userId)
				.orElseThrow(UserNotFoundException::new);

		return userMapper.toDto(user);
	}

}
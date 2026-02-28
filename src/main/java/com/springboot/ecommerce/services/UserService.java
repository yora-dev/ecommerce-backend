package com.springboot.ecommerce.services;

import com.springboot.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException("User not found"));

		return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
	}
}
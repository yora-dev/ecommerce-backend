package com.springboot.ecommerce.services;

import com.springboot.ecommerce.config.JwtConfig;
import com.springboot.ecommerce.entities.Role;
import com.springboot.ecommerce.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
	private JwtConfig jwtConfig;

	private String generateToken(User user, long tokenExpiration) {
		return Jwts.builder()
				.subject(user.getId().toString())
				.claim("username", user.getUsername())
				.claim("email", user.getEmail())
				.claim("role", user.getRole())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
				.signWith(jwtConfig.getSecretKey())
				.compact();
	}

	public String generateAccessToken(User user) {
		return generateToken(user, jwtConfig.getAccessTokenExpiration());
	}

	public String generateRefreshToken(User user) {
		return generateToken(user, jwtConfig.getRefreshTokenExpiration());
	}

	public boolean isTokenValid(String token) {

		try {
			var claims = getClaims(token);
			return claims.getExpiration().after(new Date());
		} catch (Exception ex) {
			return false;
		}

	}

	public Long extractUserId(String token) {
		return Long.valueOf(getClaims(token).getSubject());
	}

	public Role extractUserRole(String token) {
		var claims = getClaims(token);
		return Role.valueOf(claims.get("role", String.class));
	}

	private Claims getClaims(String token) {
		return Jwts.parser()
				.verifyWith(jwtConfig.getSecretKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
package com.springboot.ecommerce.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")

@Data
public class JwtConfig {
	@Value("${spring.jwt.secret-key}")
	private String secretKey;

	@Value("${spring.jwt.accessTokenExpiration}")
	private int accessTokenExpiration;

	@Value("${spring.jwt.refreshTokenExpiration}")
	private int refreshTokenExpiration;

	public SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}
}
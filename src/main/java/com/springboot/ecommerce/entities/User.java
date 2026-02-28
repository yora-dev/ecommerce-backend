package com.springboot.ecommerce.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "username")
	private String username;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public boolean isSeller() {
		return role != null && "SELLER".equalsIgnoreCase(role.getName());
	}

	public boolean isAdmin() {
		return role != null && "ADMIN".equalsIgnoreCase(role.getName());
	}

	public boolean isCustomer() {
		return role != null && "CUSTOMER".equalsIgnoreCase(role.getName());
	}
}

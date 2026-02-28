package com.springboot.ecommerce.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_profiles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class SellerProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "store_name")
	private String storeName;

	@Column(name = "description")
	private String description;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

//	@Column(name = "verified")
//	private boolean verified;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}

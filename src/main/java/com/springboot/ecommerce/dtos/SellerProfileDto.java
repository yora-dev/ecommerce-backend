package com.springboot.ecommerce.dtos;

import com.springboot.ecommerce.entities.SellerProfile;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SellerProfileDto {
	private Long id;
	private String storeName;
	private String description;
	private Long userId;
	private LocalDateTime createdAt;

	public SellerProfileDto toDto(SellerProfile dto) {
		this.id = dto.getId();
		this.storeName = dto.getStoreName();
		this.description = dto.getDescription();
		this.userId = dto.getUser().getId();
		this.createdAt = dto.getCreatedAt();

		return this;
	}
}

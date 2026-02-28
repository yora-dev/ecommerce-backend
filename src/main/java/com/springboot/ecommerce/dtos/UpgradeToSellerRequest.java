package com.springboot.ecommerce.dtos;

import lombok.Data;

@Data
public class UpgradeToSellerRequest {
	private Long userId;
	private String storeName;
	private String description;
}

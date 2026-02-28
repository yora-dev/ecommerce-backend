package com.springboot.ecommerce.repositories;

import com.springboot.ecommerce.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}

package com.carwash.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carwash.entity.User;
import com.carwash.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	boolean existsByRole(Role role);
	List<User> findByRole(Role customer);
	List<User> findByFullNameContainingIgnoreCase(String name);
}

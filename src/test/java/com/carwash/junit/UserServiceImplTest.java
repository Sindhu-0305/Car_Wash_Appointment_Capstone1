package com.carwash.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.carwash.dto.UserRegisterRequest;
import com.carwash.entity.User;
import com.carwash.enums.Role;
import com.carwash.repository.ServiceProviderRepository;
import com.carwash.repository.UserRepository;
import com.carwash.service.UserServiceImpl;

public class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ServiceProviderRepository serviceProviderRepository;

	@Mock
	private PasswordEncoder encoder;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	void testUpdateUser_Unauthorized() {
		Long targetId = 1L;

		User target = new User();
		target.setUserId(1L);

		User updater = new User();
		updater.setUserId(2L);
		updater.setRole(Role.CUSTOMER);

		when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
		when(userRepository.findByEmail("yuva@gmail.com")).thenReturn(Optional.of(updater));

		UserRegisterRequest req = new UserRegisterRequest();
		req.setFullName("Yuva");

		assertThrows(AccessDeniedException.class, () -> userService.updateUser(targetId, req, "yuva@gmail.com"));
	}
}

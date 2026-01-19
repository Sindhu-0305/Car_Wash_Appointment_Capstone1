package com.carwash.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.carwash.entity.User;
import com.carwash.enums.Role;
import com.carwash.enums.UserStatus;
import com.carwash.repository.UserRepository;

@Component
public class AdminBootstrap implements CommandLineRunner{
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		if(!userRepository.existsByRole(Role.ADMIN)) {
			User user = new User();
			user.setFullName("System Admin");
			user.setEmail("admin123@gmail.com");
			user.setPhoneNumber("456432346");
			user.setPassword(passwordEncoder.encode("admin12345"));
			user.setRole(Role.ADMIN);
			user.setStatus(UserStatus.ACTIVE);
			user.setCreatedAt(LocalDateTime.now());

			userRepository.save(user);
	        System.out.println("Default Admin Created!");
		}
		
	}

}

package com.carwash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carwash.dto.LoginRequest;
import com.carwash.dto.LoginResponse;
import com.carwash.entity.User;
import com.carwash.repository.UserRepository;
import com.carwash.security.JwtUtil;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid Credentials");
		}
		String token = jwtUtil.generateToken(user);
		return new LoginResponse(token, user.getRole().name());
	}
}

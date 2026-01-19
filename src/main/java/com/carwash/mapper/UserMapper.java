package com.carwash.mapper;

import com.carwash.entity.User;
import com.carwash.enums.Role;
import com.carwash.enums.UserStatus;

import java.time.LocalDateTime;

import com.carwash.dto.UserRegisterRequest;
import com.carwash.dto.UserRegisterResponse;

public class UserMapper {

	public UserMapper() {
	
	}
	public static User RequestToUser(UserRegisterRequest request) {
		User user = new User();
		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setPhoneNumber(request.getPhoneNumber());
		user.setPassword(request.getPassword());
		user.setCreatedAt(LocalDateTime.now());
		return user;
		
	}
	public static UserRegisterResponse UserToResponse(User user) {
		UserRegisterResponse response = new UserRegisterResponse();
		response.setUserId(user.getUserId());
		response.setFullName(user.getFullName());
		response.setRole(user.getRole().name());
		response.setStatus(user.getStatus().name());
		response.setCreatedAt(user.getCreatedAt());
		return response;
		
	}

	
}

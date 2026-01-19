package com.carwash.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.carwash.dto.ProfileRequest;
import com.carwash.dto.ServiceProviderRequest;
import com.carwash.dto.UserRegisterRequest;
import com.carwash.entity.User;

@Service
public interface UserService {

	ResponseEntity<?> registerCustomer(UserRegisterRequest request);

	ResponseEntity<?> registerServiceProvider(Long adminId, ServiceProviderRequest request);

	ResponseEntity<?> registerAdmin(Long adminId, UserRegisterRequest request);

	ResponseEntity<List<User>> getAllCustomers();

	ResponseEntity<List<ServiceProviderRequest>> getAllServiceProviders();

	ProfileRequest getMyProfile(String email);
	
	ProfileRequest getUserById(Long id);

	ProfileRequest getUserByEmail(String email);

    ResponseEntity<List<ProfileRequest>> getUserByName(String name);

	ResponseEntity<List<ProfileRequest>> getAllUsers();

	void updateUser(Long id, UserRegisterRequest request, String name);

	void deactivateUser(Long id);

	void activateUser(Long id);

	void deleteUser(Long id);


}

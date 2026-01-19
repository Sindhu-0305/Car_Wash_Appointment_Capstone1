package com.carwash.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carwash.dto.ProfileRequest;
import com.carwash.dto.ServiceProviderRequest;
import com.carwash.dto.UserRegisterRequest;
import com.carwash.dto.UserRegisterResponse;
import com.carwash.entity.ServiceProvider;
import com.carwash.entity.User;
import com.carwash.enums.Role;
import com.carwash.enums.UserStatus;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.mapper.UserMapper;
import com.carwash.repository.ServiceProviderRepository;
import com.carwash.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public ResponseEntity<?> registerCustomer(UserRegisterRequest request) {
		// TODO Auto-generated method stub
		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.badRequest().body("Email already exists");
		}
		User user = UserMapper.RequestToUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.CUSTOMER);
		user.setStatus(UserStatus.ACTIVE);
		User savedUser = userRepository.save(user);

		return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.UserToResponse(savedUser));
	}

	@Override
	public ResponseEntity<?> registerServiceProvider(Long adminId, ServiceProviderRequest request) {
		// TODO Auto-generated method stub
		Optional<User> admin = userRepository.findById(adminId);
		if (admin.isEmpty() || admin.get().getRole() != Role.ADMIN) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin can create service provider");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");

		}
		User spuser = new User();
		spuser.setFullName(request.getFullName());
		spuser.setEmail(request.getEmail());
		spuser.setPhoneNumber(request.getPhoneNumber());
		spuser.setPassword(passwordEncoder.encode(request.getPassword()));
		spuser.setRole(Role.SERVICE_PROVIDER);
		spuser.setStatus(UserStatus.ACTIVE);
		spuser.setCreatedAt(LocalDateTime.now());
		User savedUser = userRepository.save(spuser);

		ServiceProvider sprovider = new ServiceProvider();
		sprovider.setUser(savedUser);
		sprovider.setSpecialization(request.getSpecialization());
		sprovider.setExperienceYears(request.getExperienceYears());
		sprovider.setRating(0.0);

		serviceProviderRepository.save(sprovider);
		UserRegisterResponse response = new UserRegisterResponse();
		response.setUserId(sprovider.getId());
		response.setFullName(sprovider.getUser().getFullName());
		response.setRole(sprovider.getUser().getRole().name());
		response.setStatus(sprovider.getUser().getStatus().name());
		response.setCreatedAt(sprovider.getUser().getCreatedAt());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

	@Override
	public ResponseEntity<?> registerAdmin(Long adminId, UserRegisterRequest request) {
		// TODO Auto-generated method stub
		Optional<User> admin = userRepository.findById(adminId);
		if (admin.get().getRole() != Role.ADMIN) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin can create admin");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);

		}
		User adminUser = new User();
		adminUser.setFullName(request.getFullName());
		adminUser.setEmail(request.getEmail());
		adminUser.setPhoneNumber(request.getPhoneNumber());
		adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
		adminUser.setRole(Role.ADMIN);
		adminUser.setStatus(UserStatus.ACTIVE);
		adminUser.setCreatedAt(LocalDateTime.now());

		userRepository.save(adminUser);

		UserRegisterResponse response = new UserRegisterResponse();
		response.setUserId(adminUser.getUserId());
		response.setFullName(adminUser.getFullName());
		response.setRole(adminUser.getRole().name());
		response.setStatus(adminUser.getStatus().name());
		response.setCreatedAt(adminUser.getCreatedAt());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Override

	public ResponseEntity<List<User>> getAllCustomers() {
		return ResponseEntity.ok(userRepository.findByRole(Role.CUSTOMER));
	}

	@Override
	public ResponseEntity<List<ServiceProviderRequest>> getAllServiceProviders() {

		List<ServiceProvider> providers = serviceProviderRepository.findAll();
		List<ServiceProviderRequest> list = new ArrayList<>();

		for (ServiceProvider sp : providers) {
			User u = sp.getUser();

			ServiceProviderRequest request = new ServiceProviderRequest(u != null ? u.getFullName() : null,
					u != null ? u.getEmail() : null, null, u != null ? u.getPhoneNumber() : null,
					sp.getSpecialization(), sp.getExperienceYears());
			list.add(request);
		}
		return ResponseEntity.ok(list);

	}

	public ProfileRequest getMyProfile(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return new ProfileRequest(user.getUserId(), user.getEmail(), user.getRole().name());
	}

	@Override
	public ProfileRequest getUserById(Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return new ProfileRequest(user.getUserId(), user.getEmail(), user.getRole().name());

	}

	@Override
	public ProfileRequest getUserByEmail(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return new ProfileRequest(user.getUserId(), user.getEmail(), user.getRole().name());

	}

	@Override
	public ResponseEntity<List<ProfileRequest>> getUserByName(String name) {

		List<User> users = userRepository.findByFullNameContainingIgnoreCase(name);
		List<ProfileRequest> list = new ArrayList<>();

		for (User u : users) {
			list.add(new ProfileRequest(u.getUserId(), u.getEmail(), u.getRole().name()));
		}

		return ResponseEntity.ok(list);
	}

	@Override
	public ResponseEntity<List<ProfileRequest>> getAllUsers() {

		List<User> users = userRepository.findAll();
		List<ProfileRequest> list = new ArrayList<>();

		for (User u : users) {
			list.add(new ProfileRequest(u.getUserId(), u.getEmail(), u.getRole().name()));
		}

		return ResponseEntity.ok(list);
	}

	@Override
	public void updateUser(Long id, UserRegisterRequest request, String updatedBy) {
		User target = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Target not found"));

		User user = userRepository.findByEmail(updatedBy).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		boolean isAdmin = user.getRole() == Role.ADMIN;
		boolean isSelf = user.getUserId().equals(target.getUserId());

		if (!(isAdmin || isSelf)) {
			throw new AccessDeniedException("You are not allowed to update this user");
		}

		if (request.getFullName() != null) {
			target.setFullName(request.getFullName());
		}

		if (request.getPhoneNumber() != null) {
			target.setPhoneNumber(request.getPhoneNumber());
		}

		userRepository.save(target);
	}

	@Override
	public void deactivateUser(Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setStatus(UserStatus.INACTIVE);
		userRepository.save(user);
	}

	@Override
	public void activateUser(Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setStatus(UserStatus.ACTIVE);
		userRepository.save(user);
	}

	@Override
	public void deleteUser(Long id) {

		boolean exists = userRepository.existsById(id);
		if (!exists) {
			throw new ResourceNotFoundException("User not found");
		}

		userRepository.deleteById(id);
	}

}

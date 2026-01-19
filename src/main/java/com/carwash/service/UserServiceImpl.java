package com.carwash.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public ResponseEntity<?> registerCustomer(UserRegisterRequest request) {
		// TODO Auto-generated method stub
		log.info("Registering new customer email={}", request.getEmail());
		if (userRepository.existsByEmail(request.getEmail())) {
			log.warn("Customer registration failed — email already exists {}", request.getEmail());
			return ResponseEntity.badRequest().body("Email already exists");
		}
		User user = UserMapper.RequestToUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.CUSTOMER);
		user.setStatus(UserStatus.ACTIVE);
		User savedUser = userRepository.save(user);
		log.info("Customer registered successfully userId={}", savedUser.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.UserToResponse(savedUser));
	}

	@Override
	public ResponseEntity<?> registerServiceProvider(Long adminId, ServiceProviderRequest request) {
		// TODO Auto-generated method stub
		log.info("Admin {} creating service provider email={}", adminId, request.getEmail());
		Optional<User> admin = userRepository.findById(adminId);
		if (admin.isEmpty() || admin.get().getRole() != Role.ADMIN) {
			log.warn("Unauthorized attempt to create service provider by adminId={}", adminId);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin can create service provider");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			log.warn("Service provider creation failed — email exists {}", request.getEmail());
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
		log.info("Service provider created successfully providerId={}", sprovider.getId());
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
		log.info("Admin {} attempting to create another admin email={}", adminId, request.getEmail());
		Optional<User> admin = userRepository.findById(adminId);
		if (admin.get().getRole() != Role.ADMIN) {
			log.warn("Unauthorized admin-create by adminId={}", adminId);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin can create admin");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			log.warn("Admin creation failed — email exists {}", request.getEmail());
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
		log.info("Admin created successfully adminId={}", adminUser.getUserId());

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
		log.info("Fetching all customers");

		return ResponseEntity.ok(userRepository.findByRole(Role.CUSTOMER));
	}

	@Override
	public ResponseEntity<List<ServiceProviderRequest>> getAllServiceProviders() {
		log.info("Fetching all service providers");
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
		log.info("Fetching profile for email={}", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return new ProfileRequest(user.getUserId(), user.getEmail(), user.getRole().name());
	}

	@Override
	public ProfileRequest getUserById(Long id) {
		log.info("Fetching user by id={}", id);

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return new ProfileRequest(user.getUserId(), user.getEmail(), user.getRole().name());

	}

	@Override
	public ProfileRequest getUserByEmail(String email) {
		log.info("Fetching user by email={}", email);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return new ProfileRequest(user.getUserId(), user.getEmail(), user.getRole().name());

	}

	@Override
	public ResponseEntity<List<ProfileRequest>> getUserByName(String name) {
		log.info("Searching users by name={}", name);

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
		log.info("Updating user id={} by {}", id, updatedBy);

		User target = userRepository.findById(id).orElseThrow(() -> {
			log.error("Target user not found id={}", id);
			return new ResourceNotFoundException("Target not found");
		});

		User user = userRepository.findByEmail(updatedBy).orElseThrow(() -> {
			log.error("Updating user not found email={}", updatedBy);
			return new ResourceNotFoundException("User not found");
		});

		boolean isAdmin = user.getRole() == Role.ADMIN;
		boolean isSelf = user.getUserId().equals(target.getUserId());

		if (!(isAdmin || isSelf)) {
			log.warn("User {} attempted unauthorized update on userId={}", updatedBy, id);
			throw new AccessDeniedException("You are not allowed to update this user");
		}

		if (request.getFullName() != null) {
			target.setFullName(request.getFullName());
		}

		if (request.getPhoneNumber() != null) {
			target.setPhoneNumber(request.getPhoneNumber());
		}

		userRepository.save(target);
		log.info("User updated successfully id={}", id);
	}

	@Override
	public void deactivateUser(Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setStatus(UserStatus.INACTIVE);
		userRepository.save(user);
		log.info("User deactivated id={}", id);
	}

	@Override
	public void activateUser(Long id) {

		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		user.setStatus(UserStatus.ACTIVE);
		userRepository.save(user);
		log.info("User activated id={}", id);

	}

	@Override
	public void deleteUser(Long id) {

		boolean exists = userRepository.existsById(id);
		if (!exists) {
			throw new ResourceNotFoundException("User not found");
		}

		userRepository.deleteById(id);
		log.info("User deleted id={}", id);
	}

}

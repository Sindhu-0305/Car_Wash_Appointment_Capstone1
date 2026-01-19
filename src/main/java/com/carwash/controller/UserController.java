package com.carwash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.dto.ProfileRequest;
import com.carwash.dto.ServiceProviderRequest;
import com.carwash.dto.UserRegisterRequest;
import com.carwash.entity.User;
import com.carwash.repository.UserRepository;
import com.carwash.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
    private UserRepository userRepository;

	@Autowired
	private UserService userService;


	@PostMapping("/register")
	public ResponseEntity<?> registerCustomer(@Valid @RequestBody UserRegisterRequest request) {
		return userService.registerCustomer(request);
	}

	@PostMapping("/service-provider/register")
	public ResponseEntity<?> registerServiceProvider(@Valid @RequestParam("userId") Long adminId,
			@RequestBody ServiceProviderRequest request) {
		return userService.registerServiceProvider(adminId, request);
	}

	@PostMapping("/admin/register")
	public ResponseEntity<?> registerServiceProvider(@Valid @RequestParam("userId") Long adminId,
			@RequestBody UserRegisterRequest request) {
		return userService.registerAdmin(adminId, request);
	}

	@GetMapping("/all-customers")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<User>> getAllCustomers() {
		return userService.getAllCustomers();
	}

	@GetMapping("/all-service-providers")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ServiceProviderRequest>> getAllServiceProviders() {
		return userService.getAllServiceProviders();
	}

	@GetMapping("/my-profile")
	@PreAuthorize("hasAnyRole('ADMIN','SERVICE_PROVIDER','CUSTOMER')")
	public ResponseEntity<?> getMyProfile(Authentication authentication) {
		String email = authentication.getName();
		return ResponseEntity.ok(userService.getMyProfile(email));
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getUserById(@PathVariable Long id){
		return ResponseEntity.ok(userService.getUserById(id));
	}
	
	@GetMapping("/email/{email}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getUserByEmail(@PathVariable String email){
		return ResponseEntity.ok(userService.getUserByEmail(email));
	}
	
	@GetMapping("/name/{name}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ProfileRequest>> getUserByName(@PathVariable String name){
		return userService.getUserByName(name);
	}
	
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ProfileRequest>> getAllUsers(){
		return userService.getAllUsers();
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','SERVICE_PROVIDER','CUSTOMER')")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRegisterRequest request, Authentication authentication){
		userService.updateUser(id,request, authentication.getName());
		return ResponseEntity.ok("Profile updated successfully");
	}
	
	@PutMapping("/{id}/deactivate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deactivateUser(@PathVariable Long id){
		userService.deactivateUser(id);
		return ResponseEntity.ok("User deactivated");
	}

	
	@PutMapping("/{id}/activate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> activateUser(@PathVariable Long id){
		userService.activateUser(id);
		return ResponseEntity.ok("User activated");
	}
	
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteUser(@PathVariable Long id){
		userService.deleteUser(id);
		return ResponseEntity.ok("User deleted successfully");
	}
	
	

}
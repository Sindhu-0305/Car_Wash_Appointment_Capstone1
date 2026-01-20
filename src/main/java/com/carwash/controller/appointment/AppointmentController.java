package com.carwash.controller.appointment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.dto.appointment.AppointmentRequest;
import com.carwash.dto.appointment.AppointmentResponse;
import com.carwash.dto.appointment.ServiceProviderAvailability;
import com.carwash.enums.appointment.BookingStatus;
import com.carwash.service.appointment.AppointmentService;

import jakarta.validation.Valid;

@RestController
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;

	@GetMapping("/bookings/providers/recent")
	public ResponseEntity<List<ServiceProviderAvailability>> recentProviders(Authentication authentication,
			@RequestParam Long serviceItemId, @RequestParam LocalDateTime scheduledAt) {
		return ResponseEntity
				.ok(appointmentService.recentProvidersOptions(authentication.getName(), serviceItemId, scheduledAt));
	}

	@GetMapping("/bookings/providers/suggestions")
	public ResponseEntity<List<ServiceProviderAvailability>> suggestions(Authentication authentication,
			@RequestParam Long serviceItemId, @RequestParam LocalDateTime scheduledAt,
			@RequestParam(required = false) Integer limit) {
		return ResponseEntity
				.ok(appointmentService.suggestedProviders(authentication.getName(), serviceItemId, scheduledAt, limit));
	}

	@PostMapping("/bookings")
	public ResponseEntity<AppointmentResponse> create(Authentication authentication,
			@Valid @RequestBody AppointmentRequest req) {
		return ResponseEntity.ok(appointmentService.create(authentication.getName(), req));
	}

	@GetMapping("/bookings/my")
	public ResponseEntity<List<AppointmentResponse>> myBookings(Authentication authentication) {
		return ResponseEntity.ok(appointmentService.getMyBookings(authentication.getName()));
	}

	@PutMapping("/bookings/{id}/cancel")
	public ResponseEntity<?> cancel(Authentication authentication, @PathVariable Long id) {
		appointmentService.cancelMyBooking(authentication.getName(), id);
		return ResponseEntity.ok("Booking cancelled");
	}

	@GetMapping("/service-provider/bookings")
	public ResponseEntity<List<AppointmentResponse>> providerBookings(Authentication authentication) {
		return ResponseEntity.ok(appointmentService.getProviderBookings(authentication.getName()));
	}

	@PutMapping("/service-provider/bookings/{id}/accept")
	public ResponseEntity<?> accept(Authentication authentication, @PathVariable Long id) {
		appointmentService.accept(authentication.getName(), id);
		return ResponseEntity.ok("Booking accepted");
	}

	@PutMapping("/service-provider/bookings/{id}/reject")
	public ResponseEntity<?> reject(Authentication authentication, @PathVariable Long id) {
		appointmentService.reject(authentication.getName(), id);
		return ResponseEntity.ok("Booking rejected");
	}

	@PutMapping("/service-provider/bookings/{id}/complete")
	public ResponseEntity<?> complete(Authentication authentication, @PathVariable Long id) {
		appointmentService.complete(authentication.getName(), id);
		return ResponseEntity.ok("Booking completed");
	}

	@GetMapping("/admin/bookings")
	public ResponseEntity<List<AppointmentResponse>> all() {
		return ResponseEntity.ok(appointmentService.getAll());
	}

	@GetMapping("/admin/bookings/{id}")
	public ResponseEntity<AppointmentResponse> get(@PathVariable Long id) {
		return ResponseEntity.ok(appointmentService.getById(id));
	}

	@PutMapping("/admin/bookings/{id}/status")
	public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
		appointmentService.updateStatus(id, status.name());
		return ResponseEntity.ok("Status updated");
	}

}

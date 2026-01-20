package com.carwash.controller.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.dto.payment.PaymentPreviewResponse;
import com.carwash.dto.payment.PaymentResponse;
import com.carwash.service.payment.PaymentService;

@RestController
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@GetMapping("/payments/preview")
	public ResponseEntity<PaymentPreviewResponse> preview(Authentication auth, @RequestParam Long appointmentId,
			@RequestParam(required = false) String offerCode) {
		return ResponseEntity.ok(paymentService.preview(auth.getName(), appointmentId, offerCode));
	}

	@PostMapping("/payments/pay")
	public ResponseEntity<PaymentResponse> pay(Authentication auth, @RequestParam Long appointmentId,
			@RequestParam(defaultValue = "UPI") String method, @RequestParam(required = false) String offerCode) {
		return ResponseEntity.ok(paymentService.pay(auth.getName(), appointmentId, method, offerCode));
	}

	@GetMapping("/payments/appointment/{id}")
	public ResponseEntity<?> getPaymentByAppointment(@PathVariable Long id) {
		return ResponseEntity.ok(paymentService.getByAppointmentId(id));
	}

}

package com.carwash.controller.feedback;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.dto.feedback.FeedbackCreateRequest;
import com.carwash.dto.feedback.FeedbackResponse;
import com.carwash.dto.feedback.ProviderFeedbackSummary;
import com.carwash.service.feedback.FeedbackService;

import jakarta.validation.Valid;

@RestController
public class FeedbackController {

	@Autowired
	private FeedbackService service;

	 @PostMapping("/feedbacks")
	    @PreAuthorize("hasRole('CUSTOMER')")
	    public ResponseEntity<FeedbackResponse> createFeedback(
	            Authentication authentication,
	            @Valid @RequestBody FeedbackCreateRequest req) {
	        String email = authentication.getName();
	        FeedbackResponse res = service.createFeedback(email, req);
	        return ResponseEntity.ok(res);
	    }

	    @GetMapping("/me")
	    @PreAuthorize("hasRole('CUSTOMER')")
	    public ResponseEntity<List<FeedbackResponse>> myFeedbacks(Authentication authentication) {
	        String email = authentication.getName();
	        return ResponseEntity.ok(service.getMyFeedbacks(email));
	    }


	    @GetMapping("/provider/{providerId}/rating-summary")
	    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','SERVICE_PROVIDER')")
	    public ResponseEntity<ProviderFeedbackSummary> providerRating(@PathVariable Long providerId) {
	        return ResponseEntity.ok(service.getProviderRatingSummary(providerId));
	    }

	    
	    @GetMapping("/admin/feedbacks")
	    @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<List<FeedbackResponse>> all() {
	        return ResponseEntity.ok(service.getAll());
	    }

}

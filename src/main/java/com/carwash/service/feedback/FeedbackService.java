package com.carwash.service.feedback;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carwash.dto.feedback.FeedbackCreateRequest;
import com.carwash.dto.feedback.FeedbackResponse;
import com.carwash.dto.feedback.ProviderFeedbackSummary;

@Service
public interface FeedbackService {

	FeedbackResponse createFeedback(String customerEmail, FeedbackCreateRequest req);

	List<FeedbackResponse> getMyFeedbacks(String customerEmail);

	List<FeedbackResponse> getByProvider(Long providerId);

	List<FeedbackResponse> getAll();

	ProviderFeedbackSummary getProviderRatingSummary(Long providerId);

	void refreshProviderRating(Long providerId);

}

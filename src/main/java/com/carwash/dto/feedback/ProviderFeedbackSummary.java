package com.carwash.dto.feedback;

public class ProviderFeedbackSummary {
	private Long providerId;
	private Double averageRating;
	private Long totalReviews;

	public ProviderFeedbackSummary() {
	}

	public ProviderFeedbackSummary(Long providerId, Double averageRating, Long totalReviews) {
		this.providerId = providerId;
		this.averageRating = averageRating;
		this.totalReviews = totalReviews;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Long getTotalReviews() {
		return totalReviews;
	}

	public void setTotalReviews(Long totalReviews) {
		this.totalReviews = totalReviews;
	}

}
